package plugins.NetworkNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import components.Node;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import interfaces.ContentNodeAddressI;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkFacade.NodeManagementPI;
import plugins.NetworkFacade.port_connector.FacadeOutboundPort;
import plugins.NetworkFacade.port_connector.FacadeServiceConnector;
import plugins.NetworkNode.port_connector.NodeInboundPort;
import plugins.NetworkNode.port_connector.NodeOutboundPort;
import plugins.NetworkNode.port_connector.NodeServiceConnector;
import plugins.NetworkScanner.NetworkScannerPlugin;
import utiles.Displayer;

public class NodePlugin
    extends AbstractPlugin {

  // The port used to connect to the NodeManagement component.
  protected FacadeOutboundPort NMGetterPort;

  // The port used to be called by other nodes component.
  protected NodeInboundPort NSetterPort;

  private String URI;
  protected ContentManagementPlugin ContentManagementPlug;
  protected NetworkScannerPlugin NetworkScannerPlug;
  // A map of all the peers that this node is connected to.
  protected Map<String, NodeOutboundPort> peersGetterPorts;
  private ReentrantLock lock = new ReentrantLock();
  private String NMReflectionInboundURI;

  public NodePlugin(String NMReflectionInboundURI, String NodeURI, ContentManagementPlugin ContentManagementPlug,
      NetworkScannerPlugin NetworkScannerPlug) throws Exception {
    super();
    this.URI = NodeURI;
    setPluginURI(AbstractPort.generatePortURI());
    this.ContentManagementPlug = ContentManagementPlug;
    this.NetworkScannerPlug = NetworkScannerPlug;
    this.NMReflectionInboundURI = NMReflectionInboundURI;
    this.peersGetterPorts = new HashMap<>();
  }

  @Override
  public void initialise() throws Exception {
    this.NSetterPort = new NodeInboundPort(URI, this.getPluginURI(), this.getOwner(),
        this.getPreferredExecutionServiceURI());
    this.NSetterPort.publishPort();

    this.NMGetterPort = new FacadeOutboundPort(this.getOwner());
    this.NMGetterPort.publishPort();

    ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
    rop.publishPort();

    this.getOwner().doPortConnection(
        rop.getPortURI(),
        NMReflectionInboundURI,
        ReflectionConnector.class.getCanonicalName());

    String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NodeManagementPI.class);
    if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
      System.out.println("NOPE");
    } else {
      this.getOwner().doPortConnection(NMGetterPort.getPortURI(), otherInboundPortUI[0],
          FacadeServiceConnector.class.getCanonicalName());
    }
    this.getOwner().doPortDisconnection(rop.getPortURI());
    rop.unpublishPort();
    rop.destroyPort();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(NodePI.class);
    this.addRequiredInterface(NodePI.class);
    this.addRequiredInterface(NodeManagementPI.class);
  }

  public void joinNetwork() throws Exception {
    // Displayer.display(((Node) this.getOwner()).getNodeURI() + " is joining : ",
    // true);
    NMGetterPort.join(((Node) this.getOwner()).getContentNode());
  }

  public void leaveNetwork() throws Exception {
    lock.lock();
    Displayer.display(
        ((Node) this.getOwner()).getContentNode().getNodeURI() + " is leaving : " + this.peersGetterPorts.size(), true);
    NMGetterPort.leave(((Node) this.getOwner()).getContentNode());
    for (String peerPortURI : this.peersGetterPorts.keySet()) {
      NodeOutboundPort out = peersGetterPorts.getOrDefault(peerPortURI, null);
      if (out != null) {
        out.disconnect(((Node) this.getOwner()).getContentNode());
      }
    }
    this.peersGetterPorts.clear();
    lock.unlock();
  }

  public String getNMOutboundPortURI() throws Exception {
    return NMGetterPort.getPortURI();
  }

  /**
   * It connects to the peer node, adds it to the content management and network
   * scanner plugs, and stores the outbound port in the peersGetterPorts map
   * 
   * @param node the node to add to the network
   * @return The node that was added to the network.
   */
  public void connect(PeerNodeAddressI node) throws Exception {
    lock.lock();
    NodeOutboundPort peerOutPortN = new NodeOutboundPort(this.getOwner());
    peerOutPortN.publishPort();

    this.getOwner().doPortConnection(peerOutPortN.getPortURI(), node.getNodeURI(),
        NodeServiceConnector.class.getCanonicalName());

    peerOutPortN.share(((Node) this.getOwner()).getContentNode());
    this.peersGetterPorts.put(node.getNodeURI(), peerOutPortN);
    peerOutPortN.acceptConnected(((Node) this.getOwner()).getContentNode());
    lock.unlock();
  }

  /**
   * It deletes a peer from the network and alert others plugins
   * 
   * @param node the node to be deleted from the network
   */
  public void disconnect(PeerNodeAddressI node) throws Exception {
    lock.lock();
    NodeOutboundPort outBoundPort = this.peersGetterPorts.get(node.getNodeURI());
    this.getOwner().doPortDisconnection(outBoundPort.getPortURI());
    outBoundPort.unpublishPort();
    this.peersGetterPorts.remove(node.getNodeURI());
    ContentManagementPlug.remove(node);
    NetworkScannerPlug.remove(node);
    lock.unlock();
  }

  @Override
  public void finalise() throws Exception {
    super.finalise();
    this.getOwner().doPortDisconnection(NMGetterPort.getPortURI());
  }

  public NodeInboundPort getNodeInboundPort() {
    return this.NSetterPort;
  };

  public void acceptNeighbours(Set<PeerNodeAddressI> neighbours) throws Exception {
    for (PeerNodeAddressI peerNodeAddressI : neighbours) {
      if (peerNodeAddressI.getNodeIdentifier() != ((Node) (this.getOwner())).getContentNode().getNodeIdentifier()) {
        //System.out.println("\t\t\t\t" + ((Node) this.getOwner()).getContentNode().getNodeIdentifier() + "<->"+ peerNodeAddressI.getNodeIdentifier());
        connect(peerNodeAddressI);
      }

    }
  }

  public void acceptConnected(PeerNodeAddressI node) throws Exception {
    NodeOutboundPort peerOutPortN = new NodeOutboundPort(this.getOwner());
    peerOutPortN.publishPort();

    this.getOwner().doPortConnection(peerOutPortN.getPortURI(), node.getNodeURI(),
        NodeServiceConnector.class.getCanonicalName());
        
    this.peersGetterPorts.put(node.getNodeURI(), peerOutPortN);
  }

  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen, int count)
      throws Exception {
    lock.lock();
    // System.out.println("NB SAUTS : " + remainingHops + ", SIZE : " +
    // peersGetterPorts.size() + ", CHOSEN NULL ? " + (chosen==null ? "True":
    // "False") );
    if (remainingHops <= 0 || this.peersGetterPorts.size() == 0) {
      NMGetterPort.acceptProbed((chosen == null ? ((Node) this.getOwner()).getContentNode()
          : (count > peersGetterPorts.size() ? ((Node) this.getOwner()).getContentNode() : chosen)), requestURI);
      lock.unlock();
      return;
    }
    int randindex = new Random().nextInt(peersGetterPorts.size());
    List<NodeOutboundPort> ports = new ArrayList<>(this.peersGetterPorts.values());
    NodeOutboundPort chosenNeighbour = ports.get(randindex);
    if (chosen == null || count > peersGetterPorts.size()) {
      chosenNeighbour.probe(requestURI, facade, remainingHops - 1, ((Node) this.getOwner()).getContentNode(),
          peersGetterPorts.size());
    } else {
      chosenNeighbour.probe(requestURI, facade, remainingHops - 1, chosen, count);
    }
    lock.unlock();
  }
  public void share(ContentNodeAddressI a) throws Exception {
    ContentManagementPlug.put(a);
  }
}
