package plugins.NetworkNode.port_connector;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

import interfaces.PeerNodeAddressI;
import plugins.NetworkNode.NodePI;

public class NodeOutboundPort
    extends AbstractOutboundPort
    implements NodePI {
  public NodeOutboundPort(ComponentI owner) throws Exception {
    super(generatePortURI(), NodePI.class, owner);
  }

  @Override
  public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
    return ((NodePI) this.getConnector()).connect(a);
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    ((NodePI) this.getConnector()).disconnect(a);
  }

}
