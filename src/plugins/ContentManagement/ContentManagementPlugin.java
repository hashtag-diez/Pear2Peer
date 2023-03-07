package plugins.ContentManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import connectors.ClientReturnConnector;
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
import interfaces.PeerNodeAddressI;
import plugins.ContentManagement.port_connector.CMInboundPort;
import plugins.ContentManagement.port_connector.CMOutboundPort;
import plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import ports.ClientOutboundPort;

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
    this.loadDescriptors(6 + DescriptorId, addr);
    setPluginURI(AbstractPort.generatePortURI());
  }

  @Override
  public void initialise() throws Exception {
    this.getterPorts = new HashMap<>();

    this.setterPort = new CMInboundPort(this.getPluginURI(), this.getOwner());
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

  public void remove(PeerNodeAddressI node) throws Exception {
    this.getterPorts.remove(node);
    CMOutboundPort outBoundPortCM = get(node);
    getOwner().doPortDisconnection(outBoundPortCM.getPortURI());
    outBoundPortCM.unpublishPort();
  }

  public void loadDescriptors(int number, ContentNodeAddressI addr) throws Exception {
    ContentDataManager.DATA_DIR_NAME = "src/data";
    ArrayList<HashMap<String, Object>> result = ContentDataManager.readDescriptors(number);
    for (HashMap<String, Object> obj : result) {
      ContentDescriptorI readDescriptor = new ContentDescriptor(obj, (ContentNodeAddressI) addr);
      contentsDescriptors.add(readDescriptor);
    }
  }

  public void find(ContentTemplateI request, int hops, String returnAddr) throws Exception {

    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(request)) {
        ClientOutboundPort clientOutboundPort = new ClientOutboundPort(this.getOwner());
        clientOutboundPort.publishPort();
        this.getOwner().doPortConnection(clientOutboundPort.getPortURI(), returnAddr,
            ClientReturnConnector.class.getCanonicalName());
        clientOutboundPort.findResult(localCd);
        this.getOwner().doPortDisconnection(clientOutboundPort.getPortURI());
        clientOutboundPort.unpublishPort();
        return;
      }
    }
    if (hops-- == 0)
      return;

    for (PeerNodeAddressI node : this.getterPorts.keySet()) {
      CMOutboundPort outBoundPort = getterPorts.get(node);
      ((ContentManagementPI) outBoundPort).find(request,
          hops, returnAddr);
    }

  }

  public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, String returnAddr)
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
              hops, returnAddr);
        }
      }
    }

    ClientOutboundPort clientOutboundPort = new ClientOutboundPort(this.getOwner());
    clientOutboundPort.publishPort();
    this.getOwner().doPortConnection(clientOutboundPort.getPortURI(), returnAddr,
        ClientReturnConnector.class.getCanonicalName());
    clientOutboundPort.matchResult(matched);
    this.getOwner().doPortDisconnection(clientOutboundPort.getPortURI());
    clientOutboundPort.unpublishPort();
  }

  public boolean containsKey(PeerNodeAddressI a) {
    return this.getterPorts.containsKey(a);
  }
}
