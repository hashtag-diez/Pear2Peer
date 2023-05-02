package plugins.ContentManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import components.Node;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import implem.ContentDescriptor;
import interfaces.ContentDescriptorI;
import interfaces.ContentManagementNodeAddressI;
import interfaces.ContentNodeAddressI;
import interfaces.ContentTemplateI;
import interfaces.ApplicationNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagement.port_connector.ContentManagementInboundPort;
import plugins.ContentManagement.port_connector.ContentManagementOutboundPort;
import plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import plugins.FacadeContentManagement.port_connector.FacadeContentManagementOutboundPort;
import plugins.FacadeContentManagement.port_connector.FacadeContentManagementServiceConnector;
import utiles.Helpers;

public class ContentManagementPlugin
    extends AbstractPlugin {

  protected String URI;
  protected ContentManagementInboundPort setterPort;
  protected Map<String, ContentManagementOutboundPort> getterPorts = new HashMap<>();;
  protected List<ContentDescriptorI> contentsDescriptors;
  protected int PINGED = 4;

  public List<ContentDescriptorI> getContentsDescriptors() {
    return contentsDescriptors;
  }

  public ContentManagementPlugin(
      String URI, int DescriptorId, ContentManagementNodeAddressI addr) throws Exception {
    super();
    contentsDescriptors = new ArrayList<>();
    this.URI = URI;
    this.loadDescriptors(DescriptorId, addr);
    setPluginURI(AbstractPort.generatePortURI());
  }

  @Override
  public void initialise() throws Exception {
    this.setterPort = new ContentManagementInboundPort(URI, this.getPluginURI(), this.getOwner(),
        this.getPreferredExecutionServiceURI());
    this.setterPort.publishPort();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(ContentManagementPI.class);
    this.addRequiredInterface(ContentManagementPI.class);
  }

  /**
   * It connects to the peer node via its reflectionOutboundPort,
   * gets its ContentManagementPlugin Port, connects to it, and
   * stores the connection in a map
   * 
   * @param node the node to connect to
   */
  public void put(ContentNodeAddressI node) throws Exception {
    ContentManagementOutboundPort peerOutPortCM = new ContentManagementOutboundPort(this.getOwner());
    peerOutPortCM.publishPort();

    this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), node.getContentManagementURI(),
        ContentManagementServiceConnector.class.getCanonicalName());
    this.getterPorts.put(node.getContentManagementURI(), peerOutPortCM);
    peerOutPortCM.acceptShared(((Node) this.getOwner()).getContentNode());
  }

  /**
   * It removes the node from the list of nodes that the owner of the
   * `GetterPorts` object can get data
   * from
   * 
   * @param node the node to remove
   */
  public void remove(PeerNodeAddressI node) throws Exception {
    ContentManagementOutboundPort outBoundPortCM = this.getterPorts.remove(node.getNodeURI());
    if (outBoundPortCM == null) /* Si il leave en même temps */
      return;
    getOwner().doPortDisconnection(outBoundPortCM.getPortURI());
    outBoundPortCM.unpublishPort();
  }

  public void loadDescriptors(int number, ContentManagementNodeAddressI addr) throws Exception {
    ContentDataManager.DATA_DIR_NAME = "src/data";
    ArrayList<HashMap<String, Object>> result = ContentDataManager.readDescriptors(number);
    for (HashMap<String, Object> obj : result) {
      ContentDescriptorI readDescriptor = new ContentDescriptor(obj, addr);
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
  public void find(ContentTemplateI cd, int hops, ApplicationNodeAddressI requester, String clientAddr)
      throws Exception {
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        FacadeContentManagementOutboundPort port = makeFacadeOutboundPort(requester);
        port.acceptFound(localCd, clientAddr);
        return;
      }
    }
    if (--hops == 0)
      return;

    Collection<ContentManagementOutboundPort> ports = Helpers.getRandomCollection(this.getterPorts.values(), PINGED);
    for (ContentManagementOutboundPort outBoundPort : ports)
      outBoundPort.find(cd, hops, requester, clientAddr);

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
  public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, ApplicationNodeAddressI requester,
      String clientAddr)
      throws Exception {
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        matched.add(localCd);
      }
    }

    if (--hops == 0) {
      FacadeContentManagementOutboundPort port = makeFacadeOutboundPort(requester);
      port.acceptMatched(matched, clientAddr);
      return;
    }
    Collection<ContentManagementOutboundPort> ports = Helpers.getRandomCollection(this.getterPorts.values(), PINGED);
    for (ContentManagementOutboundPort outBoundPort : ports)
      outBoundPort.match(cd, matched, hops, requester, clientAddr);

  }

  public boolean containsKey(PeerNodeAddressI a) {
    return this.getterPorts.containsKey(a.getNodeURI());
  }

  private FacadeContentManagementOutboundPort makeFacadeOutboundPort(ApplicationNodeAddressI addr) throws Exception {

    FacadeContentManagementOutboundPort outboundPort = new FacadeContentManagementOutboundPort(this.getOwner());
    outboundPort.publishPort();

    this.getOwner().doPortConnection(outboundPort.getPortURI(), addr.getContentManagementURI(),
        FacadeContentManagementServiceConnector.class.getCanonicalName());
    return outboundPort;
  }

  public void acceptShared(ContentManagementNodeAddressI connected) throws Exception {
    ContentManagementOutboundPort peerOutPortCM = new ContentManagementOutboundPort(this.getOwner());
    peerOutPortCM.publishPort();
    this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), connected.getContentManagementURI(),
        ContentManagementServiceConnector.class.getCanonicalName());

    this.getterPorts.put(connected.getContentManagementURI(), peerOutPortCM);
  }
}
