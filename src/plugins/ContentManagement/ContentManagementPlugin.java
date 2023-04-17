package plugins.ContentManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import implem.ContentDescriptor;
import interfaces.ContentDescriptorI;
import interfaces.ContentNodeAddressI;
import interfaces.ContentTemplateI;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagement.FacadeContentManagement.FacadeContentManagementPI;
import plugins.ContentManagement.FacadeContentManagement.port_connector.CMFacadeOutboundPort;
import plugins.ContentManagement.FacadeContentManagement.port_connector.CMFacadeServiceConnector;
import plugins.ContentManagement.port_connector.CMInboundPort;
import plugins.ContentManagement.port_connector.CMOutboundPort;
import plugins.ContentManagement.port_connector.ContentManagementServiceConnector;

public class ContentManagementPlugin
    extends AbstractPlugin {

  protected CMInboundPort setterPort;
  protected Map<PeerNodeAddressI, CMOutboundPort> getterPorts;
  protected List<ContentDescriptorI> contentsDescriptors;

  public List<ContentDescriptorI> getContentsDescriptors() {
    return contentsDescriptors;
  }

  public ContentManagementPlugin(
      int DescriptorId, ContentNodeAddressI addr) throws Exception {
    super();
    contentsDescriptors = new ArrayList<>();
    this.loadDescriptors(DescriptorId, addr);
    setPluginURI(AbstractPort.generatePortURI());
  }

  @Override
  public void initialise() throws Exception {
    this.getterPorts = new HashMap<>();

    this.setterPort = new CMInboundPort(this.getPluginURI(), this.getOwner(), this.getPreferredExecutionServiceURI());
    this.setterPort.publishPort();
  }

  public String pluginPortUri() throws Exception {
    return this.setterPort.getPortURI();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(ContentManagementPI.class);
  }

  /**
   * It connects to the peer node via its reflectionOutboundPort,
   * gets its ContentManagementPlugin Port, connects to it, and
   * stores the connection in a map
   * 
   * @param node the node to connect to
   */
  public void put(PeerNodeAddressI node) throws Exception {
    CMOutboundPort peerOutPortCM = new CMOutboundPort(this.getOwner());
    peerOutPortCM.publishPort();

    ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
    rop.publishPort();

    this.getOwner().doPortConnection(
        rop.getPortURI(),
        node.getNodeURI(),
        ReflectionConnector.class.getCanonicalName());

    String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(ContentManagementPI.class);
    if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
      System.out.println("NOPE");
    } else {
      this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), otherInboundPortUI[0],
          ContentManagementServiceConnector.class.getCanonicalName());
    }
    this.getOwner().doPortDisconnection(rop.getPortURI());
    rop.unpublishPort();
    rop.destroyPort();
    this.getterPorts.put(node, peerOutPortCM);
  }

  public CMOutboundPort get(PeerNodeAddressI node) {
    CMOutboundPort outBoundPortCM = this.getterPorts.get(node);
    return outBoundPortCM;
  }

  /**
   * It removes the node from the list of nodes that the owner of the
   * `GetterPorts` object can get data
   * from
   * 
   * @param node the node to remove
   */
  public void remove(PeerNodeAddressI node) throws Exception {
    this.getterPorts.remove(node);
    CMOutboundPort outBoundPortCM = get(node);
    if (outBoundPortCM != null) {
      getOwner().doPortDisconnection(outBoundPortCM.getPortURI());
      outBoundPortCM.unpublishPort();
    }
  }

  public void loadDescriptors(int number, ContentNodeAddressI addr) throws Exception {
    ContentDataManager.DATA_DIR_NAME = "src/data";
    ArrayList<HashMap<String, Object>> result = ContentDataManager.readDescriptors(number);
    for (HashMap<String, Object> obj : result) {
      ContentDescriptorI readDescriptor = new ContentDescriptor(obj, (ContentNodeAddressI) addr);
      contentsDescriptors.add(readDescriptor);
    }
  }

  /**
   * The function `find` is used to find a content descriptor that matches the
   * request. If the content
   * descriptor is found, the result is sent to the client. If the content
   * descriptor is not found and
   * enough hops are available, the request is forwarded to the next peer
   * 
   * @param request    the content template to match
   * @param hops       the number of hops to go through
   * @param returnAddr the address of the client that made the request
   */
  public void find(ContentTemplateI cd, int hops, FacadeNodeAddressI requester, String clientAddr) throws Exception {
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        CMFacadeOutboundPort port = makeFacadeOutboundPort(requester);
        port.acceptFound(localCd, clientAddr);
      }
    }
    if (hops-- == 0)
      return;

    for (PeerNodeAddressI node : this.getterPorts.keySet()) {
      CMOutboundPort outBoundPort = getterPorts.get(node);
      ((ContentManagementPI) outBoundPort).find(cd,
          hops, requester, clientAddr);
    }
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
  public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, FacadeNodeAddressI requester,
      String clientAddr)
      throws Exception {
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        matched.add(localCd);
      }
    }

    if (hops-- != 0) {
      for (PeerNodeAddressI node : this.getterPorts.keySet()) {
        CMOutboundPort outBoundPort = getterPorts.get(node);
        if (outBoundPort != null) {
          ((ContentManagementPI) outBoundPort).match(cd, matched,
              hops, requester, clientAddr);
        }
      }
    } else {
      CMFacadeOutboundPort port = makeFacadeOutboundPort(requester);
      port.acceptMatched(matched, clientAddr);
    }
  }

  public boolean containsKey(PeerNodeAddressI a) {
    return this.getterPorts.containsKey(a);
  }

  private CMFacadeOutboundPort makeFacadeOutboundPort(FacadeNodeAddressI addr) throws Exception {

    CMFacadeOutboundPort outboundPort = new CMFacadeOutboundPort(this.getOwner());
    ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
    rop.publishPort();

    this.getOwner().doPortConnection(
        rop.getPortURI(),
        addr.getNodeURI(),
        ReflectionConnector.class.getCanonicalName());

    String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(FacadeContentManagementPI.class);
    if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
      System.out.println("NOPE");
    } else {
      this.getOwner().doPortConnection(outboundPort.getPortURI(), otherInboundPortUI[0],
          CMFacadeServiceConnector.class.getCanonicalName());
    }
    this.getOwner().doPortDisconnection(rop.getPortURI());
    rop.unpublishPort();
    rop.destroyPort();
    return outboundPort;
  }
}
