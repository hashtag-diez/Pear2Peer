package main.java.plugins.NetworkScanner.port_connector;

import java.util.HashMap;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import main.java.interfaces.NodeAddressI;
import main.java.plugins.NetworkScanner.NetworkScannerPI;
import main.java.plugins.NetworkScanner.NodeInformationI;

public class NetworkScannerServiceConnector extends AbstractConnector
        implements NetworkScannerPI {

    @Override
    public HashMap<NodeAddressI, NodeInformationI> mapNetwork(HashMap<NodeAddressI, NodeInformationI> before)
            throws Exception {
        return ((NetworkScannerPI) this.offering).mapNetwork(before);
    }

}
