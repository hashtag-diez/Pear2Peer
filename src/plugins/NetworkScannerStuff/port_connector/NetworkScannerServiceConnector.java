package plugins.NetworkScannerStuff.port_connector;

import java.util.HashMap;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.NodeAddressI;
import plugins.NetworkScannerStuff.NetworkScannerPI;
import plugins.NetworkScannerStuff.NodeInformationI;

public class NetworkScannerServiceConnector extends AbstractConnector
        implements NetworkScannerPI {

    @Override
    public HashMap<NodeAddressI, NodeInformationI> mapNetwork(HashMap<NodeAddressI, NodeInformationI> before)
            throws Exception {
        return ((NetworkScannerPI) this.offering).mapNetwork(before);
    }

}
