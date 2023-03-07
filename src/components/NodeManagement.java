package components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkScannerStuff.NetworkScannerPlugin;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import ports.NodeManagementInboundPort;

@OfferedInterfaces(offered = { NodeManagementCI.class, ContentManagementPI.class })
@RequiredInterfaces(required = { ContentManagementPI.class })
public class NodeManagement
    extends AbstractComponent
    implements FacadeNodeAddressI {

  protected NodeManagementInboundPort NMSetterPort;

  protected Set<PeerNodeAddressI> members = new HashSet<>();
  protected String uriPrefix = "NodeC";
  protected ContentManagementPlugin ContentManagementPlug;
  protected NetworkScannerPlugin NetworkScannerPlug;

  protected NodeManagement(String reflectionInboundPortURI, String inboundURI, int DescriptorId) throws Exception {
    super(reflectionInboundPortURI, 4, 0);
    this.NMSetterPort = new NodeManagementInboundPort(inboundURI, this);
    this.NMSetterPort.publishPort();

    ContentManagementPlug = new ContentManagementPlugin("cm" + inboundURI, DescriptorId);
    this.installPlugin(ContentManagementPlug);

    NetworkScannerPlug = new NetworkScannerPlugin("ns" + inboundURI);
    this.installPlugin(NetworkScannerPlug);

    this.uriPrefix = this.uriPrefix + UUID.randomUUID();
  }

  public synchronized Set<PeerNodeAddressI> addNewComers(PeerNodeAddressI a) throws Exception {
    List<PeerNodeAddressI> neighbors = new ArrayList<>(members);
    if (members.size() % 4 == 0) {
      System.out.println("Nouvelle racine !");
      ContentManagementPlug.put(a);
      NetworkScannerPlug.put(a);
    }
    members.add(a);

    Set<PeerNodeAddressI> res = neighbors
        .stream()
        .skip(neighbors.size() > 0 ? neighbors.size() - 1 : 0)
        .limit(1)
        .collect(Collectors.toSet());
    return res;
  }

  /**
   * It removes a peer from the network
   * 
   * @param a the peer to be deleted
   */
  public void deletePeer(PeerNodeAddressI a) throws Exception {
    ContentManagementPlug.remove(a);
    NetworkScannerPlug.remove(a);
    members.remove(a);
  }

  @Override
  public boolean isFacade() {
    return true;
  }

  @Override
  public boolean isPeer() {
    return false;
  }

  @Override
  public String getNodeIdentifier() throws Exception {
    return NMSetterPort.getPortURI();
  }

  @Override
  public String getNodeManagementURI() {
    return uriPrefix;
  }

  @Override
  public PluginI getPlugin(Plugins toGet) {
    switch (toGet) {
      case ContentManagementPlugin:
        return ContentManagementPlug;

      case NetworkScannerPlugin:
        return NetworkScannerPlug;
      default:
        break;

    }
    throw new UnsupportedOperationException("Unimplemented plugin on node management");
  }

  @Override
  public String getPluginPort(Plugins portToGet) {
    switch (portToGet) {
      case ContentManagementPlugin:
        return ContentManagementPlug.getPluginURI();

      case NetworkScannerPlugin:
        return NetworkScannerPlug.getPluginURI();

      default:
        break;

    }
    throw new UnsupportedOperationException("Unimplemented plugin on node management");
  }

}
