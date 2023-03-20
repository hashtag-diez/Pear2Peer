package plugins.NetworkFacade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkFacade.port_connector.FacadeInboundPort;
import plugins.NetworkScanner.NetworkScannerPlugin;
import utiles.Displayer;

public class NodeManagementPlugin
    extends AbstractPlugin {
  private static final boolean DEBUG_MODE = true;

  protected FacadeInboundPort NMSetterPort;
  protected ContentManagementPlugin ContentManagementPlug;
  protected NetworkScannerPlugin NetworkScannerPlug;
  protected Set<PeerNodeAddressI> members = new HashSet<>();
  public NodeManagementPlugin(ContentManagementPlugin ContentManagementPlug, NetworkScannerPlugin NetworkScannerPlug) throws Exception {
    super();
    setPluginURI(AbstractPort.generatePortURI());
    this.ContentManagementPlug = ContentManagementPlug;
    this.NetworkScannerPlug = NetworkScannerPlug;
  }

  @Override
  public void initialise() throws Exception {
    this.NMSetterPort = new FacadeInboundPort(this.getPluginURI(),this.getOwner());
    this.NMSetterPort.publishPort();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(NodeManagementPI.class);
  }

  public synchronized Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
    Displayer.display(a.getNodeURI() + " veut se connecter au reseau.", DEBUG_MODE);
    List<PeerNodeAddressI> neighbors = new ArrayList<>(members);
    if (members.size() % 4 == 0) {
      Displayer.display("Nouvelle racine !", DEBUG_MODE);
      ContentManagementPlug.put(a);
      NetworkScannerPlug.put(a);
    }
    members.add(a);

    Set<PeerNodeAddressI> res = neighbors.stream().skip(neighbors.size() > 0 ? neighbors.size() - 1 : 0).limit(1)
        .collect(Collectors.toSet());
    return res;
  }

  /**
   * It removes a peer from the network
   * 
   * @param a the peer to be deleted
   */
  public void leave(PeerNodeAddressI a) throws Exception {
    Displayer.display(a.getNodeURI() + " veut se deconnecter du reseau.", DEBUG_MODE);
    ContentManagementPlug.remove(a);
    NetworkScannerPlug.remove(a);
    members.remove(a);
  }
}
