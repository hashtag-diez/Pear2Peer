package plugins.NetworkScannerStuff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import interfaces.ContentDescriptorI;
import interfaces.NodeAddressI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkScannerStuff.port_connector.NSPInBoundPort;
import plugins.NetworkScannerStuff.port_connector.NSPoutBoundPort;
import plugins.NetworkScannerStuff.port_connector.NetworkScannerServiceConnector;
import plugins.PluginOwnerI.Plugins;

public class NetworkScannerPlugin extends AbstractPlugin {

    public NSPInBoundPort setterPort;
    protected Map<NodeAddressI, NSPoutBoundPort> getterPorts = new HashMap<>();;
    protected String setterPortUri = null;

    public NetworkScannerPlugin(String portUri) throws Exception {
        super();
        setterPortUri = portUri;
        setPluginURI(AbstractPort.generatePortURI());
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
        if (setterPortUri == null)
            setterPortUri = this.getPluginURI();

        this.setterPort = new NSPInBoundPort(setterPortUri, this.getOwner());
        this.setterPort.publishPort();

    }

    @Override
    public void installOn(ComponentI owner) throws Exception {
        super.installOn(owner);
        this.addOfferedInterface(NetworkScannerPI.class);
        this.addRequiredInterface(NetworkScannerPI.class);
    }

    public void put(NodeAddressI node) throws Exception {

        String iport = node.getPluginPort(Plugins.NetworkScannerPlugin);
        NSPoutBoundPort peerOutPortCM = new NSPoutBoundPort(getOwner());
        peerOutPortCM.publishPort();
        getOwner().doPortConnection(peerOutPortCM.getPortURI(), iport,
                NetworkScannerServiceConnector.class.getCanonicalName());
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
        ContentManagementPlugin contentManager = (ContentManagementPlugin) owner
                .getPlugin(NodeAddressI.Plugins.ContentManagementPlugin);

        NodeInformationI info = generateNodeInformation(owner, contentManager.getContentsDescriptors());
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
