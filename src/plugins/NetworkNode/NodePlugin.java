package plugins.NetworkNode;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import components.Node;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
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

  protected ContentManagementPlugin ContentManagementPlug;
  protected NetworkScannerPlugin NetworkScannerPlug;
  // A map of all the peers that this node is connected to.
  protected ConcurrentMap<PeerNodeAddressI, NodeOutboundPort> peersGetterPorts;
  private ReentrantLock lock = new ReentrantLock();
  private String NMReflectionInboundURI;

  public NodePlugin(String NMReflectionInboundURI, ContentManagementPlugin ContentManagementPlug,
      NetworkScannerPlugin NetworkScannerPlug) throws Exception {
    super();
    setPluginURI(AbstractPort.generatePortURI());
    this.ContentManagementPlug = ContentManagementPlug;
    this.NetworkScannerPlug = NetworkScannerPlug;
    this.NMReflectionInboundURI = NMReflectionInboundURI;
    this.peersGetterPorts = new ConcurrentHashMap<>();
  }

  @Override
  public void initialise() throws Exception {
    this.NSetterPort = new NodeInboundPort(this.getPluginURI(), this.getOwner(), this.getPreferredExecutionServiceURI());
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
      System.out.println(otherInboundPortUI[0]);
      System.out.println(NMGetterPort.getPortURI());
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
    Displayer.display(((Node)this.getOwner()).getNodeURI() + " is joining : ", true);
    NMGetterPort.join((PeerNodeAddressI) this.getOwner());
  }

  public void leaveNetwork() throws Exception {
    Displayer.display(((Node)this.getOwner()).getNodeURI() + " is leaving : "+this.peersGetterPorts.size(), true);
    NMGetterPort.leave((PeerNodeAddressI) this.getOwner());
    for (PeerNodeAddressI peerNodeAddressI : this.peersGetterPorts.keySet()) {
      System.out.println(((Node)this.getOwner()).getNodeURI() + "<-X->" + peerNodeAddressI.getNodeURI());
      NodeOutboundPort out = peersGetterPorts.getOrDefault(peerNodeAddressI, null);
      if(out!=null){
        out.disconnect((PeerNodeAddressI) this.getOwner());
      }
    }
    this.peersGetterPorts.clear();
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
    NodeOutboundPort peerOutPortN = new NodeOutboundPort(this.getOwner());
    peerOutPortN.publishPort();

    ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
    rop.publishPort();

    this.getOwner().doPortConnection(
        rop.getPortURI(),
        node.getNodeURI(),
        ReflectionConnector.class.getCanonicalName());

    String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NodePI.class);
    if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
      System.out.println("NOPE");
    } else {
      this.getOwner().doPortConnection(peerOutPortN.getPortURI(), otherInboundPortUI[0],
          NodeServiceConnector.class.getCanonicalName());
    }
    this.getOwner().doPortDisconnection(rop.getPortURI());
    rop.unpublishPort();
    rop.destroyPort();

    ContentManagementPlug.put(node);
    NetworkScannerPlug.put(node);
    this.peersGetterPorts.put(node, peerOutPortN);
    peerOutPortN.acceptConnected((PeerNodeAddressI) this.getOwner());
  }

  /**
   * It deletes a peer from the network and alert others plugins
   * 
   * @param node the node to be deleted from the network
   */
  public void disconnect(PeerNodeAddressI node) throws Exception {
    NodeOutboundPort outBoundPort = this.peersGetterPorts.get(node);
    this.getOwner().doPortDisconnection(outBoundPort.getPortURI());
    outBoundPort.unpublishPort();
    this.peersGetterPorts.remove(node);
    ContentManagementPlug.remove(node);
    NetworkScannerPlug.remove(node);
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
      System.out.println(((Node)this.getOwner()).getNodeURI() + "<->" + peerNodeAddressI.getNodeURI());
      connect(peerNodeAddressI);
    }
  }

  public void acceptConnected(PeerNodeAddressI node) throws Exception {
    NodeOutboundPort peerOutPortN = new NodeOutboundPort(this.getOwner());
    peerOutPortN.publishPort();

    ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
    rop.publishPort();

    this.getOwner().doPortConnection(
        rop.getPortURI(),
        node.getNodeURI(),
        ReflectionConnector.class.getCanonicalName());

    String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NodePI.class);
    if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
      System.out.println("NOPE");
    } else {
      this.getOwner().doPortConnection(peerOutPortN.getPortURI(), otherInboundPortUI[0],
          NodeServiceConnector.class.getCanonicalName());
    }
    this.getOwner().doPortDisconnection(rop.getPortURI());
    rop.unpublishPort();
    rop.destroyPort();

    ContentManagementPlug.put(node);
    NetworkScannerPlug.put(node);
    this.peersGetterPorts.put(node, peerOutPortN);
  }

  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI requester)
      throws Exception {
      lock.lock();
      if (remainingHops <= 0 || this.peersGetterPorts.size() == 0) {
        NMGetterPort.acceptProbed((Node) this.getOwner(), requestURI);
        lock.unlock();
        return;
      }
      int randindex = new Random().nextInt(peersGetterPorts.size());
      List<NodeOutboundPort> ports = new ArrayList<>(this.peersGetterPorts.values());
      NodeOutboundPort chosen = ports.get(randindex);
      chosen.probe(requestURI, facade, 1, requester); 
      lock.unlock();
  }
}
