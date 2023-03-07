package plugins.NetworkScannerStuff.port_connector;

import java.util.HashMap;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.NodeAddressI;
import plugins.PluginOwnerI;
import plugins.NetworkScannerStuff.NetworkScannerPI;
import plugins.NetworkScannerStuff.NetworkScannerPlugin;
import plugins.NetworkScannerStuff.NodeInformationI;

public class NSPInBoundPort extends AbstractInboundPort
        implements NetworkScannerPI {

    public NSPInBoundPort(String uri, ComponentI owner) throws Exception {
        super(uri, NetworkScannerPI.class, owner);
    }

    @Override
    public HashMap<NodeAddressI, NodeInformationI> mapNetwork(HashMap<NodeAddressI, NodeInformationI> before)
            throws Exception {
        return this.getOwner().handleRequest(
                owner -> {
                    PluginOwnerI powner = (PluginOwnerI) owner;
                    NetworkScannerPlugin scanner = (NetworkScannerPlugin) powner
                            .getPlugin(NodeAddressI.Plugins.NetworkScannerPlugin);
                    return scanner.mapNetwork(before);
                });
    }

}
