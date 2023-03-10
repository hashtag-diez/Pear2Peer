package ports;

import components.interfaces.NodeCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

import interfaces.PeerNodeAddressI;

public class NodeOutboundPortN
    extends AbstractOutboundPort
    implements NodeCI {
  public NodeOutboundPortN(ComponentI owner) throws Exception {
    super(generatePortURI(), NodeCI.class, owner);
  }

  @Override
  public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
    return ((NodeCI) this.getConnector()).connect(a);
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    ((NodeCI) this.getConnector()).disconnect(a);
  }

}
