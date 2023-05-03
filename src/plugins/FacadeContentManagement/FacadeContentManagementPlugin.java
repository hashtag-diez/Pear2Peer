package plugins.FacadeContentManagement;

import java.util.Collection;
import java.util.Set;

import components.NodeManagement;
import components.interfaces.ClientCI;
import connectors.ClientReturnConnector;
import fr.sorbonne_u.components.ComponentI;
import implem.ApplicationNode;
import interfaces.ApplicationNodeAddressI;
import interfaces.ContentDescriptorI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.ContentManagement.port_connector.ContentManagementOutboundPort;
import plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import plugins.FacadeContentManagement.port_connector.FacadeContentManagementInboundPort;
import ports.ClientOutboundPort;
import utiles.Helpers;
import interfaces.ContentNodeAddressI;
import interfaces.ContentTemplateI;

public class FacadeContentManagementPlugin
    extends ContentManagementPlugin implements FacadeContentManagementPI {

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
  }

  @Override
  public void find(ContentTemplateI cd, int hops, ApplicationNodeAddressI requester, String clientAddr)
      throws Exception {
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        acceptFound(localCd, clientAddr);
      }
    }

    Collection<ContentManagementOutboundPort> ports = Helpers.getRandomCollection(this.getterPorts.values(), PINGED);
    for (ContentManagementOutboundPort outBoundPort : ports)
      outBoundPort.find(cd, hops, ((NodeManagement) this.getOwner()).getApplicationNode(), clientAddr);
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
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        matched.add(localCd);
      }
    }

    Collection<ContentManagementOutboundPort> ports = Helpers.getRandomCollection(this.getterPorts.values(), PINGED);
    for (ContentManagementOutboundPort outBoundPort : ports)
      outBoundPort.match(cd, matched, hops, ((NodeManagement) this.getOwner()).getApplicationNode(), clientAddr);
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

    } finally {
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

    } finally {
      this.getOwner().doPortDisconnection(cli.getPortURI());
      cli.unpublishPort();
      cli.destroyPort();
    }
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
    ContentManagementOutboundPort peerOutPortCM = new ContentManagementOutboundPort(this.getOwner());
    peerOutPortCM.publishPort();
    this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), node.getContentManagementURI(),
        ContentManagementServiceConnector.class.getCanonicalName());
    this.getterPorts.put(node.getContentManagementURI(), peerOutPortCM);
  }

}
