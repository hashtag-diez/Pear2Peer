package main.java.plugins.NetworkFacade.port_connector;

import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;
import main.java.plugins.NetworkFacade.NodeManagementPI;

public class NodeManagementOutboundPort
    extends AbstractOutboundPort
    implements NodeManagementPI {

  public NodeManagementOutboundPort(ComponentI owner) throws Exception {
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
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
      int chosenNeighbourCount)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementPI) this.getConnector()).probe(requestURI, facade, remainingHops, chosen, chosenNeighbourCount);
  }

  @Override
  public void interconnect(FacadeNodeAddressI f) throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementPI) this.getConnector()).interconnect(f);

  }

}
