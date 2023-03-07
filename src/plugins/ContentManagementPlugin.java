package plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import components.interfaces.ContentManagementCI;
import connectors.ContentManagementServiceConnector;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import implem.ContentDescriptor;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.PeerNodeAddressI;
import ports.InboundPortCM;
import ports.OutboundPortCM;

public class ContentManagementPlugin
    extends AbstractPlugin {

  public InboundPortCM CMSetterPort;
  protected Map<PeerNodeAddressI, OutboundPortCM> peersGetterPorts;
  protected List<ContentDescriptorI> contentsDescriptors = new ArrayList<>();

  public ContentManagementPlugin(
      int DescriptorId) throws Exception {
    super();
    this.loadDescriptors(6 + DescriptorId);
  }

  @Override
  public void initialise() throws Exception {
    this.peersGetterPorts = new HashMap<>();
    this.CMSetterPort = new InboundPortCM(this.getPluginURI(), this.getOwner());
    this.CMSetterPort.publishPort();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(ContentManagementCI.class);
  }

  public void put(PeerNodeAddressI node) throws Exception {
    String oportCM = AbstractOutboundPort.generatePortURI();
    OutboundPortCM peerOutPortCM = new OutboundPortCM(oportCM, this.getOwner());
    peerOutPortCM.publishPort();

    ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
    rop.publishPort();

    this.getOwner().doPortConnection(
        rop.getPortURI(),
        node.getNodeURI(),
        ReflectionConnector.class.getCanonicalName());

    String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(ContentManagementCI.class);
    if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
      System.out.println("NOPE");
    } else {
      this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), otherInboundPortUI[0],
          ContentManagementServiceConnector.class.getCanonicalName());
    }

    this.getOwner().doPortDisconnection(rop.getPortURI());
    rop.unpublishPort();
    rop.destroyPort();

    this.peersGetterPorts.put(node, peerOutPortCM);
  }

  public void remove(PeerNodeAddressI node) throws Exception {
    OutboundPortCM outBoundPortCM = this.peersGetterPorts.get(node);
    this.getOwner().doPortDisconnection(outBoundPortCM.getPortURI());
    outBoundPortCM.unpublishPort();
    this.peersGetterPorts.remove(node);
  }

  public void loadDescriptors(int number) throws Exception {
    ContentDataManager.DATA_DIR_NAME = "src/data";
    ArrayList<HashMap<String, Object>> result = ContentDataManager.readDescriptors(number);
    for (HashMap<String, Object> obj : result) {
      ContentDescriptorI readDescriptor = new ContentDescriptor(obj);
      contentsDescriptors.add(readDescriptor);
    }
  }

  public ContentDescriptorI find(ContentTemplateI request, int hops) throws Exception {
    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(request)) {
        return localCd;
      }
    }
    if (hops-- == 0)
      return null;

    for (PeerNodeAddressI node : this.peersGetterPorts.keySet()) {
      OutboundPortCM outBoundPort = peersGetterPorts.get(node);
      if (outBoundPort != null) {
        ContentDescriptorI res = ((ContentManagementCI) outBoundPort).find(request, hops);
        if (res != null)
          return res;
      }
    }

    return null;
  }

  public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
      throws Exception {

    for (ContentDescriptorI localCd : this.contentsDescriptors) {
      if (localCd.match(cd)) {
        matched.add(localCd);
      }
    }

    if (hops != 0) {
      for (PeerNodeAddressI node : this.peersGetterPorts.keySet()) {
        OutboundPortCM outBoundPort = peersGetterPorts.get(node);
        if (outBoundPort != null) {
          matched.addAll(((ContentManagementCI) outBoundPort).match(cd, matched, --hops));
        }
      }
    }
    return matched;
  }

  public boolean containsKey(PeerNodeAddressI a) {
    return this.peersGetterPorts.containsKey(a);
  }
}
