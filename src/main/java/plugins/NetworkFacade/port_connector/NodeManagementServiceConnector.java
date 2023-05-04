package main.java.plugins.NetworkFacade.port_connector;

import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;
import main.java.plugins.NetworkFacade.NodeManagementPI;

public class NodeManagementServiceConnector
    extends AbstractConnector
    implements NodeManagementPI {

  @Override
  public void join(PeerNodeAddressI a) throws Exception {
    ((NodeManagementPI) this.offering).join(a);
  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    ((NodeManagementPI) this.offering).leave(a);
  }

  @Override
  public void acceptProbed(PeerNodeAddressI peer, String requestURI)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementPI) this.offering).acceptProbed(peer, requestURI);
  }

  @Override
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
      int chosenNeighbourCount)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementPI) this.offering).probe(requestURI, facade, remainingHops, chosen, chosenNeighbourCount);
  }

  @Override
  public void interconnect(FacadeNodeAddressI f) throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementPI) this.offering).interconnect(f);
  }
}
