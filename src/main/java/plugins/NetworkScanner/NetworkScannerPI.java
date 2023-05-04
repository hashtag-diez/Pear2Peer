package main.java.plugins.NetworkScanner;

import java.util.HashMap;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import main.java.interfaces.NodeAddressI;

/**
 * The interface <code>NetworkScannerPI</code> is offered and required by
 * both facades and node components to allow network scanning
 * 
 * @author ABSSI (Team)
 *
 */

public interface NetworkScannerPI extends RequiredCI, OfferedCI {
    HashMap<NodeAddressI, NodeInformationI> mapNetwork(HashMap<NodeAddressI, NodeInformationI> before) throws Exception;
}
