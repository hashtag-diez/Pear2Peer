package plugins.NetworkScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import interfaces.ContentDescriptorI;
import interfaces.NodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkScanner.port_connector.NetworkScannerInboundPort;
import plugins.NetworkScanner.port_connector.NetworkScannerOutboundPort;
import plugins.NetworkScanner.port_connector.NetworkScannerServiceConnector;

public class NetworkScannerPlugin extends AbstractPlugin {

    public NetworkScannerInboundPort setterPort;
    protected Map<NodeAddressI, NetworkScannerOutboundPort> getterPorts = new HashMap<>();;
    private ContentManagementPlugin plugin;

    public NetworkScannerPlugin(ContentManagementPlugin plugin) throws Exception {
        super();
        setPluginURI(AbstractPort.generatePortURI());
        this.plugin = plugin;
    }

    public NetworkScannerPlugin() throws Exception {
        super();
        setPluginURI(AbstractPort.generatePortURI());
    }

    public String pluginPortUri() throws Exception {
        return this.setterPort.getPortURI();
    }

    @Override
    public void initialise() throws Exception {
        this.getterPorts = new HashMap<>();

        this.setterPort = new NetworkScannerInboundPort(this.getPluginURI(), this.getOwner());
        this.setterPort.publishPort();

    }

    @Override
    public void installOn(ComponentI owner) throws Exception {
        super.installOn(owner);
        this.addOfferedInterface(NetworkScannerPI.class);
        this.addRequiredInterface(NetworkScannerPI.class);
    }

    @Override
    public void finalise() throws Exception {
        super.finalise();
        for(NodeAddressI port : getterPorts.keySet()){
            NetworkScannerOutboundPort out = getterPorts.get(port);
            this.getOwner().doPortDisconnection(out.getPortURI());
            out.unpublishPort();
          }
        setterPort.unpublishPort();
    }
    /**
     * It creates a new outbound port, connects it to the inbound port of the node
     * we want to connect
     * to, and then adds it to the map of outbound ports
     * 
     * @param node the node to connect to
     */
    public void put(PeerNodeAddressI node) throws Exception {
        NetworkScannerOutboundPort peerOutPortCM = new NetworkScannerOutboundPort(getOwner());
        peerOutPortCM.publishPort();

        ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
        rop.publishPort();

        this.getOwner().doPortConnection(
                rop.getPortURI(),
                node.getNodeURI(),
                ReflectionConnector.class.getCanonicalName());

        String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NetworkScannerPI.class);
        if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
            System.out.println("NOPE");
        } else {
            this.getOwner().doPortConnection(peerOutPortCM.getPortURI(), otherInboundPortUI[0],
                    NetworkScannerServiceConnector.class.getCanonicalName());
        }

        this.getOwner().doPortDisconnection(rop.getPortURI());
        rop.unpublishPort();
        rop.destroyPort();
        this.getterPorts.put(node, peerOutPortCM);
    }

    public void remove(NodeAddressI node) throws Exception {
        this.getterPorts.remove(node);
        NetworkScannerOutboundPort outBoundPortCM = get(node);
        if (outBoundPortCM != null) {
            getOwner().doPortDisconnection(outBoundPortCM.getPortURI());
            outBoundPortCM.unpublishPort();
        }
    }

    public NetworkScannerOutboundPort get(NodeAddressI node) {
        NetworkScannerOutboundPort outBoundPortCM = this.getterPorts.get(node);
        return outBoundPortCM;
    }

    NodeInformationI generateNodeInformation(NodeAddressI owner, List<ContentDescriptorI> descriptors) {
        return new NodeInformation(owner.isPeer(), owner.isFacade(), this.getterPorts.keySet(), descriptors);
    }

    /**
     * It returns a map of all the nodes in the network, and their information
     * 
     * @param before the map of nodes that have already been scanned
     * @return A HashMap of NodeAddressI and NodeInformationI
     */
    public HashMap<NodeAddressI, NodeInformationI> mapNetwork(
            HashMap<NodeAddressI, NodeInformationI> before)
            throws Exception {

        NodeAddressI owner = (NodeAddressI) this.getOwner();

        NodeInformationI info = generateNodeInformation(owner, plugin.getContentsDescriptors());
        before.put(owner, info);
        for (NodeAddressI node : this.getterPorts.keySet())
            if (!before.containsKey(node)) {
                NetworkScannerOutboundPort outBoundPort = getterPorts.get(node);
                if (outBoundPort != null)
                    before.putAll(((NetworkScannerPI) outBoundPort).mapNetwork(before));
            }
        return before;
    }

}
