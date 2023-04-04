package plugins.NetworkFacade.port_connector;

import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.NetworkFacade.NodeManagementPI;

public class FacadeOutboundPort
    extends AbstractOutboundPort
    implements NodeManagementPI {

  public FacadeOutboundPort(ComponentI owner) throws Exception {
    super(generatePortURI(), NodeManagementPI.class, owner);
  }

  @Override
  public void join(PeerNodeAddressI a) throws Exception {
    ((NodeManagementPI) this.getConnector()).join(a);

  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    ((NodeManagementPI) this.getConnector()).leave(a);
  }

  @Override
  public void acceptProbed(PeerNodeAddressI peer, String requestURI)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementPI) this.getConnector()).acceptProbed(peer, requestURI);
  }

  @Override
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI requester)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementPI) this.getConnector()).probe(requestURI, facade, remainingHops, requester);
  }

}