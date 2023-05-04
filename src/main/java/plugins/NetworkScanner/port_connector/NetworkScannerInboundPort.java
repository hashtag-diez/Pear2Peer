package main.java.plugins.NetworkScanner.port_connector;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import main.java.interfaces.NodeAddressI;
import main.java.plugins.NetworkScanner.NetworkScannerPI;
import main.java.plugins.NetworkScanner.NetworkScannerPlugin;
import main.java.plugins.NetworkScanner.NodeInformationI;

public class NetworkScannerInboundPort extends AbstractInboundPort
        implements NetworkScannerPI {

    public NetworkScannerInboundPort(String pluginUri, ComponentI owner) throws Exception {
        super(NetworkScannerPI.class, owner, pluginUri,null);
    }

    @Override
    public HashMap<NodeAddressI, NodeInformationI> mapNetwork(HashMap<NodeAddressI, NodeInformationI> before)
            throws Exception {
        return this.getOwner().handleRequest(
            new AbstractComponent.AbstractService<HashMap<NodeAddressI, NodeInformationI>>(this.getPluginURI()) {
                @Override
                public HashMap<NodeAddressI, NodeInformationI> call() throws Exception {
                    return ((NetworkScannerPlugin) this.getServiceProviderReference()).mapNetwork(before);
                }
            });
    }

}
