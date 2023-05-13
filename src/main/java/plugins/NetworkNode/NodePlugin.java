package main.java.plugins.NetworkNode;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import main.java.components.Node;
import main.java.interfaces.ContentNodeAddressI;
import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;
import main.java.plugins.ContentManagement.ContentManagementPlugin;
import main.java.plugins.NetworkFacade.NodeManagementPI;
import main.java.plugins.NetworkFacade.port_connector.NodeManagementOutboundPort;
import main.java.plugins.NetworkFacade.port_connector.NodeManagementServiceConnector;
import main.java.plugins.NetworkNode.port_connector.NodeInboundPort;
import main.java.plugins.NetworkNode.port_connector.NodeOutboundPort;
import main.java.plugins.NetworkNode.port_connector.NodeServiceConnector;
// import main.java.utiles.DebugDisplayer;

public class NodePlugin
    extends AbstractPlugin implements NodePI {

  // The port used to connect to the NodeManagement component.
  protected NodeManagementOutboundPort NMGetterPort;

  // The port used to be called by other nodes component.
  protected NodeInboundPort NSetterPort;

  private String URI;
  protected ContentManagementPlugin ContentManagementPlug;
  // A map of all the peers that this node is connected to.
  protected Map<String, NodeOutboundPort> peersGetterPorts;
  private ReentrantLock lock = new ReentrantLock();
  private String NMReflectionInboundURI;

  public NodePlugin(String NMReflectionInboundURI, String NodeURI, ContentManagementPlugin ContentManagementPlug)
      throws Exception {
    super();
    this.URI = NodeURI;
    setPluginURI(AbstractPort.generatePortURI());
    this.ContentManagementPlug = ContentManagementPlug;
    this.NMReflectionInboundURI = NMReflectionInboundURI;
    this.peersGetterPorts = new HashMap<>();
  }

  @Override
  public void initialise() throws Exception {
    this.NSetterPort = new NodeInboundPort(URI, this.getPluginURI(), this.getOwner(),
        this.getPreferredExecutionServiceURI());
    this.NSetterPort.publishPort();

    this.NMGetterPort = new NodeManagementOutboundPort(this.getOwner());
    this.NMGetterPort.publishPort();
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(NodePI.class);
    this.addRequiredInterface(NodePI.class);
    this.addRequiredInterface(NodeManagementPI.class);
    this.addRequiredInterface(ReflectionCI.class);
  }

  public void joinNetwork() throws Exception {
    // ((Node) this.getOwner()).writeMessage(((Node) this.getOwner()).getContentNode().getNodeURI() + " is joining : ");
    if (!NMGetterPort.connected()) {
      ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
      rop.publishPort();

      this.getOwner().doPortConnection(
          rop.getPortURI(),
          NMReflectionInboundURI,
          ReflectionConnector.class.getCanonicalName());

      String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NodeManagementPI.class);
      if (otherInboundPortUI.length != 0 && otherInboundPortUI != null)
        this.getOwner().doPortConnection(NMGetterPort.getPortURI(), otherInboundPortUI[0],
            NodeManagementServiceConnector.class.getCanonicalName());
      else
        throw new Exception("No inbound port found for the NodeManagementPI interface.");
      this.getOwner().doPortDisconnection(rop.getPortURI());
      rop.unpublishPort();

      this.removeRequiredInterface(ReflectionCI.class);
    }
    NMGetterPort.join(((Node) this.getOwner()).getContentNode());
  }

  public void leaveNetwork() throws Exception {
    lock.lock();
    if (NMGetterPort.connected()){
      NMGetterPort.leave(((Node) this.getOwner()).getContentNode());
    }

    for (String port : this.peersGetterPorts.keySet()) {
      // peersGetterPorts.get(port).disconnect(((Node) this.getOwner()).getContentNode());
      this.getOwner().doPortDisconnection(peersGetterPorts.get(port).getPortURI());
      peersGetterPorts.get(port).unpublishPort();
      ContentManagementPlug.remove(port);
    }
    this.peersGetterPorts.clear();
    this.getOwner().doPortDisconnection(NMGetterPort.getPortURI());
    NMGetterPort.unpublishPort();
    // ((Node) this.getOwner()).writeMessage(((Node) this.getOwner()).getContentNode().getNodeIdentifier() + " is leaving : network");
    lock.unlock();
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
    if (this.peersGetterPorts.get(node.getNodeURI()) != null) {
      lock.unlock();
      return;
    }
    NodeOutboundPort peerOutPortN = new NodeOutboundPort(this.getOwner());
    peerOutPortN.publishPort();

    this.getOwner().doPortConnection(peerOutPortN.getPortURI(), node.getNodeURI(),
        NodeServiceConnector.class.getCanonicalName());
    peerOutPortN.share(((Node) this.getOwner()).getContentNode());
    this.peersGetterPorts.put(node.getNodeURI(), peerOutPortN);
    ((Node) this.getOwner()).writeMessage(((Node) this.getOwner()).getContentNode().getNodeIdentifier() + " -> " + node.getNodeIdentifier()+";");
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
    NodeOutboundPort outBoundPort = this.peersGetterPorts.remove(node.getNodeURI());
    this.getOwner().doPortDisconnection(outBoundPort.getPortURI());
    outBoundPort.unpublishPort();
    ContentManagementPlug.remove(node);
    // NetworkScannerPlug.remove(node);
    lock.unlock();
  }

  @Override
  public void finalise() throws Exception {
    super.finalise();
    if (NMGetterPort.connected())
      this.getOwner().doPortDisconnection(NMGetterPort.getPortURI());

    NMGetterPort.unpublishPort();
    NMGetterPort.destroyPort();

    NSetterPort.unpublishPort();
    NSetterPort.destroyPort();
    for (String port : this.peersGetterPorts.keySet()) {
      this.getOwner().doPortDisconnection(peersGetterPorts.get(port).getPortURI());
      peersGetterPorts.get(port).unpublishPort();
      ContentManagementPlug.remove(port);
    }
    this.peersGetterPorts.clear();
  }

  public void acceptNeighbours(Set<PeerNodeAddressI> neighbours) throws Exception {
    // System.out.println(((Node) (this.getOwner())).getContentNode().getNodeIdentifier() + " a recu ses voisins, ils sont " + neighbours.size());
    for (PeerNodeAddressI peerNodeAddressI : neighbours) {
      if (peerNodeAddressI.getNodeIdentifier() != ((Node) (this.getOwner())).getContentNode().getNodeIdentifier()) {
        connect(peerNodeAddressI);
      }
    }
  }

  public void acceptConnected(PeerNodeAddressI node) throws Exception {
    lock.lock();
    if (this.peersGetterPorts.get(node.getNodeURI()) != null) {
      lock.unlock();
      return;
    }
    NodeOutboundPort peerOutPortN = new NodeOutboundPort(this.getOwner());
    peerOutPortN.publishPort();
    ((Node) this.getOwner()).writeMessage(((Node) this.getOwner()).getContentNode().getNodeIdentifier() + " -> " + node.getNodeIdentifier()+";");
    this.getOwner().doPortConnection(peerOutPortN.getPortURI(), node.getNodeURI(),
        NodeServiceConnector.class.getCanonicalName());

    this.peersGetterPorts.put(node.getNodeURI(), peerOutPortN);
    lock.unlock();
  }

  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen, int count)
      throws Exception {
    // System.out.println("PROBE : "+ requestURI +", "+remainingHops);
    lock.lock();
    if (remainingHops <= 0 || this.peersGetterPorts.size() == 0) {
      NodeManagementOutboundPort port = new NodeManagementOutboundPort(getOwner());
      port.publishPort();
      this.getOwner().doPortConnection(port.getPortURI(), facade.getNodeManagementURI(), NodeManagementServiceConnector.class.getCanonicalName());  
      port.acceptProbed((chosen == null ? ((Node) this.getOwner()).getContentNode()
      : (count > peersGetterPorts.size() ? ((Node) this.getOwner()).getContentNode() : chosen)), requestURI);
      this.getOwner().doPortDisconnection(port.getPortURI());
      port.unpublishPort();
      port.destroyPort();
      lock.unlock();
      return;
    }
    int randindex = new Random().nextInt(peersGetterPorts.size());
    List<NodeOutboundPort> ports = new ArrayList<>(this.peersGetterPorts.values());
    NodeOutboundPort chosenNeighbour = ports.get(randindex);
    if (chosen == null || count > peersGetterPorts.size()) {
      chosenNeighbour.probe(requestURI, facade, remainingHops - 1, ((Node) this.getOwner()).getContentNode(),
          peersGetterPorts.size());
    } else{
      chosenNeighbour.probe(requestURI, facade, remainingHops - 1, chosen, count);
    }
    lock.unlock();
  }

  public void share(ContentNodeAddressI a) throws Exception {
    ContentManagementPlug.put(a);
    // NetworkScannerPlug.put(a);
  }
}
