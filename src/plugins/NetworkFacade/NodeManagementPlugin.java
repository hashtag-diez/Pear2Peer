package plugins.NetworkFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import components.NodeManagement;
import connectors.NodeServiceConnector;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.utils.Pair;
import interfaces.ContentNodeAddressI;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.FacadeContentManagement.FacadeContentManagementPlugin;
import plugins.NetworkFacade.port_connector.NodeManagementInboundPort;
import plugins.NetworkFacade.port_connector.NodeManagementOutboundPort;
import plugins.NetworkFacade.port_connector.NodeManagementServiceConnector;
import plugins.NetworkNode.port_connector.NodeOutboundPort;
import plugins.NetworkScanner.NetworkScannerPlugin;
import utiles.DebugDisplayer;
import utiles.Helpers;

public class NodeManagementPlugin
    extends AbstractPlugin {
  private static final int ndSendProbe = 3;
  private static final int nbSaut = 3;
  private static final int nbRacine = 4;

  protected NodeManagementInboundPort NMSetterPort;
  protected FacadeContentManagementPlugin ContentManagementPlug;
  protected NetworkScannerPlugin NetworkScannerPlug;
  private ReentrantLock lock = new ReentrantLock();
  private ReentrantLock lock1 = new ReentrantLock();
  protected HashMap<String, NodeManagementOutboundPort> facades = new HashMap<>();
  protected Set<PeerNodeAddressI> roots = new HashSet<>();
  protected HashMap<String, Pair<Integer, Set<PeerNodeAddressI>>> probeCollector = new HashMap<>();
  private static final boolean DEBUG_MODE = true;
  private DebugDisplayer debugPrinter = new DebugDisplayer(DEBUG_MODE);

  public NodeManagementPlugin(String URI, FacadeContentManagementPlugin ContentManagementPlug,
      NetworkScannerPlugin NetworkScannerPlug)
      throws Exception {
    super();
    setPluginURI(URI);
    this.ContentManagementPlug = ContentManagementPlug;
    this.NetworkScannerPlug = NetworkScannerPlug;
  }

  @Override
  public void initialise() throws Exception {
    this.NMSetterPort = new NodeManagementInboundPort(this.getPluginURI(), this.getOwner());
    this.NMSetterPort.publishPort();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(NodeManagementPI.class);
    this.addRequiredInterface(NodeManagementPI.class);
  }

  public void join(PeerNodeAddressI a) throws Exception {
    lock.lock();
    if (roots.size() < nbRacine) {
      NodeOutboundPort peerOutPortN = new NodeOutboundPort(this.getOwner());
      peerOutPortN.publishPort();
      this.getOwner().doPortConnection(peerOutPortN.getPortURI(), a.getNodeURI(),
          NodeServiceConnector.class.getCanonicalName());
      ContentManagementPlug.put((ContentNodeAddressI) a);
      roots.add(a);
    }
    lock.unlock();
    probe(a.getNodeURI(), ((NodeManagement) this.getOwner()).getApplicationNode(), nbSaut, null, 0);
  }

  /**
   * It removes a peer from the network
   * 
   * @param a the peer to be deleted
   */
  public void leave(PeerNodeAddressI a) throws Exception {
    lock.lock();
    debugPrinter.display(
        (a.getNodeURI() + " is leaving : network"));
    if (roots.remove(a)) {
      ContentManagementPlug.remove(a);
    }
    lock.unlock();
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

  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
      int chosenNeighbourCount)
      throws Exception {
    Pair<Integer, Set<PeerNodeAddressI>> value = new Pair<Integer, Set<PeerNodeAddressI>>(ndSendProbe,
        new HashSet<PeerNodeAddressI>());

    if (roots.size() == 0)
      return;

    probeCollector.put(requestURI, value);
    lock.lock();
    List<PeerNodeAddressI> ports = new ArrayList<>(this.roots);
    lock.unlock();
    for (int i = 0; i < ndSendProbe; i++) {
      int randindex = Helpers.getRandomNumber(ports.size());
      PeerNodeAddressI chosenNeighbour = ports.get(randindex);
      NodeOutboundPort port = new NodeOutboundPort(this.getOwner());
      port.publishPort();

      this.getOwner().doPortConnection(port.getPortURI(), chosenNeighbour.getNodeURI(),
          NodeServiceConnector.class.getCanonicalName());

      port.probe(requestURI, ((NodeManagement) this.getOwner()).getApplicationNode(), nbSaut, null, 0);
      this.getOwner().doPortDisconnection(port.getPortURI());

      port.unpublishPort();
    }
  }

  public void connectWithFacade() throws Exception {
    String[] tab = ((NodeManagement) this.getOwner()).getApplicationNode().getNodeIdentifier().split("-");
    String basename = tab[0] + "-" + tab[1];
    String facadeIdAstString = tab[2];
    assert false : "-----------------------------------> " + facadeIdAstString;
    int FacadeIndex = Integer.parseInt(facadeIdAstString);
    for (int i = 1; i <= 5; i++) {
      lock1.lock();
      if (i != FacadeIndex && facades.get(basename + "-" + i) == null) {
        try {
          NodeManagementOutboundPort facadeOutPortNM = new NodeManagementOutboundPort(this.getOwner());
          facadeOutPortNM.publishPort();

          ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
          rop.publishPort();

          this.getOwner().doPortConnection(
              rop.getPortURI(),
              basename + "-" + i,
              ReflectionConnector.class.getCanonicalName());

          String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NodeManagementPI.class);
          if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
            System.out.println("NOPE");
          } else {
            this.getOwner().doPortConnection(facadeOutPortNM.getPortURI(), otherInboundPortUI[0],
                NodeManagementServiceConnector.class.getCanonicalName());
            facades.put(basename + "-" + i, facadeOutPortNM);
            facadeOutPortNM.interconnect(((NodeManagement) this.getOwner()).getApplicationNode());
          }
          this.getOwner().doPortDisconnection(rop.getPortURI());
          rop.unpublishPort();
          rop.destroyPort();
        } catch (NullPointerException e) {
          continue;
        }
      }
      lock1.unlock();
    }
    debugPrinter
        .display(((NodeManagement) this.getOwner()).getApplicationNode().getNodeIdentifier() + " : NB DE CONNEXIONS ->"
            + facades.size());
  }

  public void interconnect(FacadeNodeAddressI f) throws Exception {
    lock1.lock();
    if (facades.get(f.getNodeIdentifier()) != null) {
      lock1.unlock();
      return;
    }

    ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
    rop.publishPort();
    this.getOwner().doPortConnection(
        rop.getPortURI(),
        f.getNodeIdentifier(),
        ReflectionConnector.class.getCanonicalName());

    String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NodeManagementPI.class);
    if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
      System.out.println("NOPE");
    } else {
      NodeManagementOutboundPort facadeOutPortNM = new NodeManagementOutboundPort(this.getOwner());
      facadeOutPortNM.publishPort();
      this.getOwner().doPortConnection(facadeOutPortNM.getPortURI(), otherInboundPortUI[0],
          NodeManagementServiceConnector.class.getCanonicalName());
      facades.put((((NodeManagement) this.getOwner()).getApplicationNode()).getNodeManagementURI(), facadeOutPortNM);
      this.getOwner().doPortDisconnection(facadeOutPortNM.getPortURI());
      facadeOutPortNM.unpublishPort();
    }
    lock1.unlock();
    this.getOwner().doPortDisconnection(rop.getPortURI());
    rop.unpublishPort();
    rop.destroyPort();
  }
}
