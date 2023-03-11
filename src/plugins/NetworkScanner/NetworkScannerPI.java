package plugins.NetworkScanner;

import java.util.HashMap;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.NodeAddressI;

public interface NetworkScannerPI extends RequiredCI, OfferedCI {
    HashMap<NodeAddressI, NodeInformationI> mapNetwork(HashMap<NodeAddressI, NodeInformationI> before) throws Exception;
}
