package ports;

import java.util.Set;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.PeerNodeAddressI;

public class NodeOutboundPortNM
    extends AbstractOutboundPort
    implements NodeManagementCI {
  public NodeOutboundPortNM(ComponentI owner) throws Exception {
    super(generatePortURI(), NodeManagementCI.class, owner);
  }

  @Override
  public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
    return ((NodeManagementCI) this.getConnector()).join(a);

  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    ((NodeManagementCI) this.getConnector()).leave(a);
  }

}
