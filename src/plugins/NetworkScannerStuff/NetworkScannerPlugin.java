package plugins.NetworkScannerStuff;

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
import plugins.NetworkScannerStuff.port_connector.NSPInBoundPort;
import plugins.NetworkScannerStuff.port_connector.NSPoutBoundPort;
import plugins.NetworkScannerStuff.port_connector.NetworkScannerServiceConnector;

public class NetworkScannerPlugin extends AbstractPlugin {

    public NSPInBoundPort setterPort;
    protected Map<NodeAddressI, NSPoutBoundPort> getterPorts = new HashMap<>();;
    private ContentManagementPlugin plugin;

    public NetworkScannerPlugin(String pluginUri, ContentManagementPlugin plugin) throws Exception {
        super();
        setPluginURI(pluginUri);
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

        this.setterPort = new NSPInBoundPort(this.getPluginURI(), this.getOwner());
        this.setterPort.publishPort();

    }

    @Override
    public void installOn(ComponentI owner) throws Exception {
        super.installOn(owner);
        this.addOfferedInterface(NetworkScannerPI.class);
        this.addRequiredInterface(NetworkScannerPI.class);
    }

    public void put(PeerNodeAddressI node) throws Exception {
        NSPoutBoundPort peerOutPortCM = new NSPoutBoundPort(getOwner());
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
        NSPoutBoundPort outBoundPortCM = get(node);
        getOwner().doPortDisconnection(outBoundPortCM.getPortURI());
        outBoundPortCM.unpublishPort();
    }

    public NSPoutBoundPort get(NodeAddressI node) {
        NSPoutBoundPort outBoundPortCM = this.getterPorts.get(node);
        return outBoundPortCM;
    }

    NodeInformationI generateNodeInformation(NodeAddressI owner, List<ContentDescriptorI> descriptors) {
        return new NodeInformation(owner.isPeer(), owner.isFacade(), this.getterPorts.keySet(), descriptors);
    }

    public HashMap<NodeAddressI, NodeInformationI> mapNetwork(
            HashMap<NodeAddressI, NodeInformationI> before)
            throws Exception {

        NodeAddressI owner = (NodeAddressI) this.getOwner();

        NodeInformationI info = generateNodeInformation(owner, plugin.getContentsDescriptors());
        before.put(owner, info);
        for (NodeAddressI node : this.getterPorts.keySet())
            if (!before.containsKey(node)) {
                NSPoutBoundPort outBoundPort = getterPorts.get(node);
                if (outBoundPort != null)
                    before.putAll(((NetworkScannerPI) outBoundPort).mapNetwork(before));
            }
        return before;
    }

}
