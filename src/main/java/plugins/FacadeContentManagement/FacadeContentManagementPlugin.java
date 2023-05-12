package main.java.plugins.FacadeContentManagement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import fr.sorbonne_u.components.ComponentI;
import main.java.components.NodeManagement;
import main.java.components.interfaces.ClientCI;
import main.java.connectors.ClientReturnConnector;
import main.java.implem.ApplicationNode;
import main.java.interfaces.ApplicationNodeAddressI;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentNodeAddressI;
import main.java.interfaces.ContentTemplateI;
import main.java.plugins.ContentManagement.ContentManagementPlugin;
import main.java.plugins.ContentManagement.port_connector.ContentManagementOutboundPort;
import main.java.plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import main.java.plugins.FacadeContentManagement.port_connector.FacadeContentManagementInboundPort;
import main.java.plugins.FacadeContentManagement.port_connector.FacadeContentManagementOutboundPort;
import main.java.plugins.FacadeContentManagement.port_connector.FacadeContentManagementServiceConnector;
import main.java.ports.ClientOutboundPort;
import main.java.utiles.Helpers;

public class FacadeContentManagementPlugin
    extends ContentManagementPlugin implements FacadeContentManagementPI {

  protected Map<String, FacadeContentManagementOutboundPort> facadeGetterPorts = new HashMap<>();;
  private ReentrantLock lock = new ReentrantLock();

  public FacadeContentManagementPlugin(String URI, int DescriptorId, ApplicationNode addr) throws Exception {
    super(URI, DescriptorId, addr);
  }

  @Override
  public void initialise() throws Exception {
    this.setterPort = new FacadeContentManagementInboundPort(URI, this.getPluginURI(), this.getOwner(),
        this.getPreferredExecutionServiceURI());
    this.setterPort.publishPort();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(FacadeContentManagementPI.class);
    this.addRequiredInterface(ClientCI.class);
  }

  @Override
  public void finalise() throws Exception {
    super.finalise();
    for(String port : facadeGetterPorts.keySet()){
      FacadeContentManagementOutboundPort out = facadeGetterPorts.get(port);
      this.getOwner().doPortDisconnection(out.getPortURI());
      out.unpublishPort();
      out.destroyPort();
    }
    facadeGetterPorts.clear();
  }

  @Override
  public void find(ContentTemplateI cd, int hops, ApplicationNodeAddressI requester, String clientAddr)
      throws Exception {
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        acceptFound(localCd, clientAddr);
      }
    }
    if (requester == null) {
      // System.out.println("On envoie aux autres façades");
      Collection<FacadeContentManagementOutboundPort> ports = Helpers
          .getRandomCollection(this.facadeGetterPorts.values(), 2);
      for (FacadeContentManagementOutboundPort outBoundPort : ports)
        outBoundPort.find(cd, hops-1, ((NodeManagement) this.getOwner()).getApplicationNode(), clientAddr);
    } else{
      // System.out.println("On a reçu une requête d'une autre façade !");
    }
    Collection<ContentManagementOutboundPort> ports = Helpers.getRandomCollection(this.getterPorts.values(), PINGED);
    for (ContentManagementOutboundPort outBoundPort : ports)
      outBoundPort.find(cd, hops,
          (requester == null ? ((NodeManagement) this.getOwner()).getApplicationNode() : requester), clientAddr);
  }

  /**
   * It connects to the peer node via its reflectionOutboundPort,
   * gets its ContentManagementPlugin Port, connects to it, and
   * stores the connection in a map
   * 
   * @param node the node to connect to
   */
  public void put(ApplicationNode node) throws Exception {
    lock.lock();
    if(this.facadeGetterPorts.get(node.getContentManagementURI())!=null){
      lock.unlock();
      return;
    }
    FacadeContentManagementOutboundPort peerOutPortCM = new FacadeContentManagementOutboundPort(this.getOwner());
    peerOutPortCM.publishPort();

    this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), node.getContentManagementURI(),
        FacadeContentManagementServiceConnector.class.getCanonicalName());
    this.facadeGetterPorts.put(node.getContentManagementURI(), peerOutPortCM);
    lock.unlock();
    peerOutPortCM.acceptShared(((NodeManagement) this.getOwner()).getApplicationNode());
  }

  /**
   * It connects to the peer node via its reflectionOutboundPort,
   * gets its ContentManagementPlugin Port, connects to it, and
   * stores the connection in a map
   * 
   * @param node the node to connect to
   */
  @Override
  public void put(ContentNodeAddressI node) throws Exception {
    lock.lock();
    if(this.getterPorts.get(node.getContentManagementURI())!=null){
      lock.unlock();
      return;
    }
    ContentManagementOutboundPort peerOutPortCM = new ContentManagementOutboundPort(this.getOwner());
    peerOutPortCM.publishPort();

    this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), node.getContentManagementURI(),
        ContentManagementServiceConnector.class.getCanonicalName());
    this.getterPorts.put(node.getContentManagementURI(), peerOutPortCM);
    lock.unlock();
  }
  /**
   * It checks if the local content descriptors match the given content
   * descriptor, if they do, it adds
   * them to the matched set. If the hops are not 0, it calls the match function
   * on the other peers. If
   * the hops are 0, it connects to the client and sends the matched set
   * 
   * @param cd         the content descriptor to match
   * @param matched    the set of content descriptors that match the query
   * @param hops       the number of hops to go through
   * @param returnAddr the address of the client that requested the match
   */
  @Override
  public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, ApplicationNodeAddressI requester,
      String clientAddr)
      throws Exception {
    // System.out.println("RECU -> "+ this.facadeGetterPorts.size());
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        matched.add(localCd);
      }
    }
    if (requester == null) {
      // System.out.println("On envoie aux autres façades");
      Collection<FacadeContentManagementOutboundPort> ports = Helpers
          .getRandomCollection(this.facadeGetterPorts.values(), 2);
      for (FacadeContentManagementOutboundPort outBoundPort : ports){
        outBoundPort.match(cd, matched, hops-1, ((NodeManagement) this.getOwner()).getApplicationNode(), clientAddr);
      }
    } else{
      // System.out.println("On a reçu une requête d'une autre façade !");
    }
    Collection<ContentManagementOutboundPort> ports = Helpers.getRandomCollection(this.getterPorts.values(), PINGED);
    for (ContentManagementOutboundPort outBoundPort : ports)
      outBoundPort.match(cd, matched, hops,
          (requester == null ? ((NodeManagement) this.getOwner()).getApplicationNode() : requester), clientAddr);
  }

  private ClientOutboundPort makeClientOutboundPort(String clientUri) throws Exception {
    ClientOutboundPort clientOutboundPort = new ClientOutboundPort(this.getOwner());
    clientOutboundPort.publishPort();
    return clientOutboundPort;
  }

  @Override
  public void acceptFound(ContentDescriptorI found, String requestOwner) throws Exception {
    ClientOutboundPort cli = makeClientOutboundPort(requestOwner);
    try {
      this.getOwner().doPortConnection(cli.getPortURI(), requestOwner, ClientReturnConnector.class.getCanonicalName());
      cli.findResult(found);
    } catch (NullPointerException e) {

    } catch(fr.sorbonne_u.components.registry.exceptions.GlobalRegistryResponseException e){

    }finally {
      this.getOwner().doPortDisconnection(cli.getPortURI());
      cli.unpublishPort();
      cli.destroyPort();
    }
  }

  @Override
  public void acceptMatched(Set<ContentDescriptorI> found, String requestOwner) throws Exception {
    ClientOutboundPort cli = makeClientOutboundPort(requestOwner);
    try {
      this.getOwner().doPortConnection(cli.getPortURI(), requestOwner, ClientReturnConnector.class.getCanonicalName());
      cli.matchResult(found);
    } catch (NullPointerException e) {

    } catch(fr.sorbonne_u.components.registry.exceptions.GlobalRegistryResponseException e){

    } finally {
      this.getOwner().doPortDisconnection(cli.getPortURI());
      cli.unpublishPort();
      cli.destroyPort();
    }
  }

  @Override
  public void acceptShared(ApplicationNode node) throws Exception {
    lock.lock();
    if(this.facadeGetterPorts.get(node.getContentManagementURI())!=null){
      lock.unlock();
      return;
    }
    FacadeContentManagementOutboundPort peerOutPortCM = new FacadeContentManagementOutboundPort(this.getOwner());
    peerOutPortCM.publishPort();

    this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), node.getContentManagementURI(),
        FacadeContentManagementServiceConnector.class.getCanonicalName());
    this.facadeGetterPorts.put(node.getContentManagementURI(), peerOutPortCM);
    lock.unlock();
  }
}
