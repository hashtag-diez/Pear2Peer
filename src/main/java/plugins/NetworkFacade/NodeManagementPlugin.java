package main.java.plugins.NetworkFacade;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import main.java.components.NodeManagement;
import main.java.connectors.NodeServiceConnector;
import main.java.interfaces.ContentNodeAddressI;
import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPlugin;
import main.java.plugins.NetworkFacade.port_connector.NodeManagementInboundPort;
import main.java.plugins.NetworkFacade.port_connector.NodeManagementOutboundPort;
import main.java.plugins.NetworkFacade.port_connector.NodeManagementServiceConnector;
import main.java.plugins.NetworkNode.NodePI;
import main.java.plugins.NetworkNode.port_connector.NodeOutboundPort;
import main.java.plugins.NetworkScanner.NetworkScannerPlugin;
import main.java.utiles.AsyncProbe;
import main.java.utiles.DebugDisplayer;

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
  protected HashMap<String, AsyncProbe> probeCollector = new HashMap<>();
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

  @Override
		public void		finalise() throws Exception
		{
			super.finalise();
      NMSetterPort.unpublishPort();
      for(String port : facades.keySet()){
        NodeManagementOutboundPort out = facades.get(port);
        this.getOwner().doPortDisconnection(out.getPortURI());
        out.unpublishPort();
      }
      for(PeerNodeAddressI racine : roots ){
        ContentManagementPlug.remove(racine);
      }
      facades.clear();
		}

  public void join(PeerNodeAddressI a) throws Exception {
    /* debugPrinter.display(
      (a.getNodeIdentifier() + " is joining : network")); */
    lock.lock();
    if (roots.size() < nbRacine) {
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
/*     debugPrinter.display(
        (a.getNodeIdentifier() + " is leaving : network")); */
    if (roots.remove(a)) {
      ContentManagementPlug.remove(a);
    }
    lock.unlock();
  }

  public void acceptProbed(PeerNodeAddressI peer, String requestURI) throws Exception {
    AsyncProbe request = probeCollector.get(requestURI);
    request.retrieve(peer);
    if (!request.isComplete()) {
      probeCollector.put(requestURI, request);
      return;
    }
    NodeOutboundPort nop = new NodeOutboundPort(this.getOwner());
    nop.publishPort();
    this.getOwner().doPortConnection(nop.getPortURI(), requestURI, NodeServiceConnector.class.getCanonicalName());
    nop.acceptNeighbours(request.getResult());
    this.getOwner().doPortDisconnection(nop.getPortURI());
    nop.unpublishPort();
  }

  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
      int chosenNeighbourCount)
      throws Exception {
    probeCollector.put(requestURI, new AsyncProbe(ndSendProbe));
    this.addRequiredInterface(NodePI.class);
    for (int i = 0; i < ndSendProbe; i++) {
      PeerNodeAddressI chosenNeighbour = getRandomElement(roots);
      if (chosenNeighbour == null) {
        return; // no neighbour to probe
      }
      NodeOutboundPort port = new NodeOutboundPort(this.getOwner());
      port.publishPort();
      this.getOwner().doPortConnection(port.getPortURI(), chosenNeighbour.getNodeURI(), NodeServiceConnector.class.getCanonicalName());
      port.probe(requestURI, ((NodeManagement) this.getOwner()).getApplicationNode(), nbSaut, null, 0);
      this.getOwner().doPortDisconnection(port.getPortURI());
      port.unpublishPort();
    }
    this.removeRequiredInterface(NodePI.class);
  }

  public void connectWithFacade() throws Exception {
    String[] tab = ((NodeManagement) this.getOwner()).getApplicationNode().getNodeIdentifier().split("-");
    String basename = tab[0] + "-" + tab[1];
    String facadeIdAstString = tab[2];
    int FacadeIndex = Integer.parseInt(facadeIdAstString);
    this.addRequiredInterface(ReflectionCI.class);
    for (int i = 1; i <= 5; i++) {
      lock1.lock();
      if (i != FacadeIndex && facades.get(basename + "-" + i) == null) {
        NodeManagementOutboundPort facadeOutPortNM = new NodeManagementOutboundPort(this.getOwner());
        facadeOutPortNM.publishPort();

        ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
        rop.publishPort();
        debugPrinter.display("\t\t\t\t\t\t CREE");
        try {
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
          debugPrinter.display("\t\t\t\t\t\t DETRUIT");
          rop.destroyPort();
        } catch (NullPointerException e) {
          rop.unpublishPort();
          debugPrinter.display("\t\t\t\t\t\t DETRUIT");
          rop.destroyPort();
        } 
      }
      lock1.unlock();
    }
    debugPrinter.display(((NodeManagement) this.getOwner()).getApplicationNode().getNodeIdentifier() + " : connect to others facades");
  }

  public void interconnect(FacadeNodeAddressI f) throws Exception {
    lock1.lock();
    if (facades.get(f.getNodeIdentifier()) != null) {
      lock1.unlock();
      return;
    }

    ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
    rop.publishPort();
    debugPrinter.display("\t\t\t\t\t\t CREE");
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
    }
    lock1.unlock();
    this.getOwner().doPortDisconnection(rop.getPortURI());
    rop.unpublishPort();
    debugPrinter.display("\t\t\t\t\t\t DETRUIT");
    rop.destroyPort();
  }

    // Get a random element from an set
    public <T> T getRandomElement(Collection<T> set) {
      int size = set.size();
      Random r = new Random();
      int item = r.nextInt(size - 0) + 0;
      int i = 0;
      for (T obj : set) {
        if (i == item) {
          return obj;
        }
        i++;
      }
      return null;
    }
}
