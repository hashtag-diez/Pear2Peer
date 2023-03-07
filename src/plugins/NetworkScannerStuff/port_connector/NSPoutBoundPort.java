package plugins.NetworkScannerStuff.port_connector;

import java.util.HashMap;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.NodeAddressI;
import plugins.NetworkScannerStuff.NetworkScannerPI;
import plugins.NetworkScannerStuff.NodeInformationI;

public class NSPoutBoundPort
    extends AbstractOutboundPort
    implements NetworkScannerPI {

  public NSPoutBoundPort(ComponentI owner) throws Exception {
    super(generatePortURI(), NetworkScannerPI.class, owner);
  }

  @Override
  public HashMap<NodeAddressI, NodeInformationI> mapNetwork(HashMap<NodeAddressI, NodeInformationI> before)
      throws Exception {
    return ((NetworkScannerPI) this.getConnector()).mapNetwork(before);
  }

}