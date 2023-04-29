package plugins.NetworkScanner.port_connector;

import java.util.HashMap;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.NodeAddressI;
import plugins.NetworkScanner.NetworkScannerPI;
import plugins.NetworkScanner.NodeInformationI;

public class NetworkScannerOutboundPort
    extends AbstractOutboundPort
    implements NetworkScannerPI {

  public NetworkScannerOutboundPort(ComponentI owner) throws Exception {
    super(generatePortURI(), NetworkScannerPI.class, owner);
  }

  @Override
  public HashMap<NodeAddressI, NodeInformationI> mapNetwork(HashMap<NodeAddressI, NodeInformationI> before)
      throws Exception {
    return ((NetworkScannerPI) this.getConnector()).mapNetwork(before);
  }

}