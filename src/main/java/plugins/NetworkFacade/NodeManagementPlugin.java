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
import main.java.implem.ApplicationNode;
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
    this.addRequiredInterface(ReflectionCI.class);
  }

  @Override
  public void finalise() throws Exception {
    NMSetterPort.unpublishPort();
    for (String port : facades.keySet()) {
      NodeManagementOutboundPort out = facades.get(port);
      // System.out.println("PORT NM DELETE : " + out.getPortURI());
      this.getOwner().doPortDisconnection(out.getPortURI());
      out.unpublishPort();
      out.destroyPort();
    }
    for (PeerNodeAddressI racine : roots) {
      ContentManagementPlug.remove(racine);
    }
    super.finalise();
  }

  public void join(PeerNodeAddressI a) throws Exception {
    /*
     * debugPrinter.display(
     * (a.getNodeIdentifier() + " is joining : network"));
     */
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
    /*
     * debugPrinter.display(
     * (a.getNodeIdentifier() + " is leaving : network"));
     */
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
    this.addRequiredInterface(NodePI.class);
    NodeOutboundPort nop = new NodeOutboundPort(this.getOwner());
    nop.publishPort();
    this.getOwner().doPortConnection(nop.getPortURI(), requestURI, NodeServiceConnector.class.getCanonicalName());
    nop.acceptNeighbours(request.getResult());
    this.getOwner().doPortDisconnection(nop.getPortURI());
    this.removeRequiredInterface(NodePI.class);
    nop.unpublishPort();
    nop.destroyPort();
  }

  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
      int chosenNeighbourCount)
      throws Exception {
    probeCollector.put(requestURI, new AsyncProbe(ndSendProbe));
    this.addRequiredInterface(NodePI.class);
    for (int i = 0; i < ndSendProbe; i++) {
      PeerNodeAddressI chosenNeighbour = getRandomElement(roots);
      if (chosenNeighbour == null) {
        this.removeRequiredInterface(NodePI.class);
        return; // no neighbour to probe
      }
      NodeOutboundPort port = new NodeOutboundPort(this.getOwner());
      port.publishPort();
      this.getOwner().doPortConnection(port.getPortURI(), chosenNeighbour.getNodeURI(),
          NodeServiceConnector.class.getCanonicalName());
      port.probe(requestURI, ((NodeManagement) this.getOwner()).getApplicationNode(), nbSaut, null, 0);
      this.getOwner().doPortDisconnection(port.getPortURI());
      port.unpublishPort();
      port.destroyPort();
    }
    this.removeRequiredInterface(NodePI.class);
  }

  public void connectWithFacade() throws Exception {
    debugPrinter.display(
        ((NodeManagement) this.getOwner()).getApplicationNode().getNodeIdentifier() + " : connect to others facades");
    String[] tab = ((NodeManagement) this.getOwner()).getApplicationNode().getNodeIdentifier().split("-");
    String basename = tab[0] + "-" + tab[1];
    String facadeIdAstString = tab[2];
    int FacadeIndex = Integer.parseInt(facadeIdAstString);
    for (int i = 1; i <= 5; i++) {
      // System.out.println(facades.get(basename + "-" + i) == null);
      if (i != FacadeIndex) {
        NodeManagementOutboundPort facadeOutPortNM = new NodeManagementOutboundPort(this.getOwner());
        facadeOutPortNM.publishPort();
        //System.out.println("CONN : PORT NM CREE : " + facadeOutPortNM.getPortURI());

        ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
        rop.publishPort();
        lock1.lock();
        try {
          this.getOwner().doPortConnection(
              rop.getPortURI(),
              basename + "-" + i,
              ReflectionConnector.class.getCanonicalName());

          String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NodeManagementPI.class);
          if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
            System.out.println("NOPE");
          } else {
            if (facades.get(basename + "-" + i) != null) {
              throw new NullPointerException("Nope");
            }
            this.getOwner().doPortConnection(facadeOutPortNM.getPortURI(), otherInboundPortUI[0],
                NodeManagementServiceConnector.class.getCanonicalName());
            facades.put(basename + "-" + i, facadeOutPortNM);
            // System.out.println("PORT NM AJOUTE : " + facadeOutPortNM.getPortURI());
            facadeOutPortNM.interconnect(((NodeManagement) this.getOwner()).getApplicationNode());
          }
          this.getOwner().doPortDisconnection(rop.getPortURI());
          /* debugPrinter.display(
            ((NodeManagement) this.getOwner()).getApplicationNode().getNodeIdentifier() + " -> " + basename + "-" + i); */
          
        } catch (Exception e) {
          if(facadeOutPortNM.connected()){
            this.getOwner().doPortDisconnection(facadeOutPortNM.getPortURI());
          }
          facadeOutPortNM.unpublishPort();
          // System.out.println("CONN : PORT NM DELETE : " + facadeOutPortNM.getPortURI());
          facadeOutPortNM.destroyPort();
        } finally{
          rop.unpublishPort();
          rop.destroyPort();
          lock1.unlock();
        }
      }
    }
  }

  public void interconnect(ApplicationNode f) throws Exception {
    NodeManagementOutboundPort facadeOutPortNM = new NodeManagementOutboundPort(this.getOwner());
    facadeOutPortNM.publishPort();
    lock1.lock();
    //System.out.println("INTER : PORT NM CREE : " + facadeOutPortNM.getPortURI() + ", " + (facades.get(f.getNodeIdentifier()) != null ? "true" : "false") );
    try {
      if (facades.get(f.getNodeIdentifier()) != null) {
        throw new NullPointerException("Nope");
      }
      this.getOwner().doPortConnection(facadeOutPortNM.getPortURI(), f.getNodeManagementURI(),
          NodeManagementServiceConnector.class.getCanonicalName());
      facades.put(f.getNodeIdentifier(), facadeOutPortNM);
      // System.out.println("PORT NM AJOUTE : " + facadeOutPortNM.getPortURI());
      /* debugPrinter.display(
        ((NodeManagement) this.getOwner()).getApplicationNode().getNodeIdentifier() + " -> " + f.getNodeIdentifier()); */
    } catch (Exception e) {
      if(facadeOutPortNM.connected()){
        this.getOwner().doPortDisconnection(facadeOutPortNM.getPortURI());
      }
      facadeOutPortNM.unpublishPort();
      // System.out.println("INTER : PORT NM DELETE : " + facadeOutPortNM.getPortURI());
      facadeOutPortNM.destroyPort();
    } finally{
      ContentManagementPlug.put(f);
      lock1.unlock();
    }
  }

  // Get a random element from an set
  public <T> T getRandomElement(Collection<T> set) {
    try{
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
    } catch(Exception e){
      return null;
    }
    
  }
}
