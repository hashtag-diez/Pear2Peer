package plugins.NetworkFacade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import connectors.NodeServiceConnector;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.utils.Pair;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkFacade.port_connector.FacadeInboundPort;
import plugins.NetworkNode.port_connector.NodeOutboundPort;
import plugins.NetworkScanner.NetworkScannerPlugin;
import utiles.Displayer;

public class NodeManagementPlugin
    extends AbstractPlugin {
  private static final boolean DEBUG_MODE = true;
  private static final int ndSendProbe = 3;
  private static final int nbSaut = 3;
  private static final int nbRacine = 4;

  protected FacadeInboundPort NMSetterPort;
  protected ContentManagementPlugin ContentManagementPlug;
  protected NetworkScannerPlugin NetworkScannerPlug;
  protected Set<PeerNodeAddressI> roots = new HashSet<>();
  protected HashMap<String, Pair<Integer, Set<PeerNodeAddressI>>> probeCollector = new HashMap<>();

  public NodeManagementPlugin(ContentManagementPlugin ContentManagementPlug, NetworkScannerPlugin NetworkScannerPlug)
      throws Exception {
    super();
    setPluginURI(AbstractPort.generatePortURI());
    this.ContentManagementPlug = ContentManagementPlug;
    this.NetworkScannerPlug = NetworkScannerPlug;
  }

  @Override
  public void initialise() throws Exception {
    this.NMSetterPort = new FacadeInboundPort(this.getPluginURI(), this.getOwner());
    this.NMSetterPort.publishPort();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(NodeManagementPI.class);
  }

  synchronized public void join(PeerNodeAddressI a) throws Exception {
    Displayer.display(a.getNodeURI() + " veut se connecter au reseau.", DEBUG_MODE);
    Displayer.display("Nombre de racines : " + roots.size(), DEBUG_MODE);

    probe(a.getNodeIdentifier(), (FacadeNodeAddressI) this.getOwner(), nbSaut, a);
    if (roots.size() < nbRacine) {
      Displayer.display("Nouvelle racine !", DEBUG_MODE);
      ContentManagementPlug.put(a);
      NetworkScannerPlug.put(a);
      roots.add(a);
    }
  }

  /**
   * It removes a peer from the network
   * 
   * @param a the peer to be deleted
   */
  public void leave(PeerNodeAddressI a) throws Exception {
    Displayer.display(a.getNodeURI() + " veut se deconnecter du reseau.", DEBUG_MODE);
    if (roots.remove(a)) {
      ContentManagementPlug.remove(a);
      NetworkScannerPlug.remove(a);
    }
  }

  public void acceptProbed(PeerNodeAddressI peer, String requestURI) throws Exception {
    Pair<Integer, Set<PeerNodeAddressI>> value = probeCollector.get(requestURI);
    Set<PeerNodeAddressI> set = value.getSecond();
    set.add(peer);
    if (value.getFirst() - 1 == 0) {
      NodeOutboundPort nop = new NodeOutboundPort(this.getOwner());
      nop.publishPort();
      this.getOwner().doPortConnection(nop.getPortURI(), requestURI, NodeServiceConnector.class.getCanonicalName());
      nop.acceptNeighbours(set);
      this.getOwner().doPortDisconnection(nop.getPortURI());
      nop.unpublishPort();
    } else {
      probeCollector.put(requestURI, new Pair<>(value.getFirst() - 1, set));
    }
  }

  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI requester)
      throws Exception {
    Pair<Integer, Set<PeerNodeAddressI>> value = new Pair<Integer, Set<PeerNodeAddressI>>(ndSendProbe,
        new HashSet<PeerNodeAddressI>());

    if (roots.size() == 0)
      return;

    probeCollector.put(requestURI, value);
    for (int i = 0; i < ndSendProbe; i++) {
      int randindex = new Random().nextInt(roots.size());
      for (int j = 0; j < roots.size(); j++) {
        for (PeerNodeAddressI toProb : roots)
          if (j == randindex) {
            NodeOutboundPort port = new NodeOutboundPort(this.getOwner());
            port.publishPort();
            this.getOwner().doPortConnection(port.getPortURI(), toProb.getNodeIdentifier(),
                NodeServiceConnector.class.getCanonicalName());

            port.probe(requestURI, (FacadeNodeAddressI) this.getOwner(), nbSaut, null);

            this.getOwner().doPortDisconnection(port.getPortURI());
            port.unpublishPort();
            break;
          }
      }

    }
  }
}
