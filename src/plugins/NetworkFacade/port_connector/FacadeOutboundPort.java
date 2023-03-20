package plugins.NetworkFacade.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.PeerNodeAddressI;
import plugins.NetworkFacade.NodeManagementPI;

public class FacadeOutboundPort
    extends AbstractOutboundPort
    implements NodeManagementPI {

  public FacadeOutboundPort(ComponentI owner) throws Exception {
    super(generatePortURI(), NodeManagementPI.class, owner);
  }
  @Override
  public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
    return ((NodeManagementPI) this.getConnector()).join(a);

  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    ((NodeManagementPI) this.getConnector()).leave(a);
  }

}
