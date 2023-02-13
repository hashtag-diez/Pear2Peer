package ports;

import java.util.Set;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.PeerNodeAddressI;

public class NodeOutboundPortNM
    extends AbstractOutboundPort
    implements NodeManagementCI {
  public NodeOutboundPortNM(String uri, ComponentI owner) throws Exception {
    super(uri, NodeManagementCI.class, owner);
  }

  @Override
  public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
    System.out.println("Appel de Join de l'outbound de " + a.getNodeIdentifier());
    return ((NodeManagementCI) this.getConnector()).join(a);

  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    System.out.println("Appel de Leave de l'outbound de " + a.getNodeIdentifier());
    ((NodeManagementCI) this.getConnector()).leave(a);
  }
}
