package connectors;

import java.util.concurrent.RejectedExecutionException;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;

public class NodeManagementServiceConnector
    extends AbstractConnector
    implements NodeManagementCI {

  @Override
  public void join(PeerNodeAddressI a) throws Exception {
    ((NodeManagementCI) this.offering).join(a);
  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    ((NodeManagementCI) this.offering).leave(a);
  }

  @Override
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen, int chosenNeighbourCount)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementCI) this.offering).probe(requestURI, facade, remainingHops, chosen, chosenNeighbourCount);

  }

  @Override
  public void acceptProbed(PeerNodeAddressI peer, String requestURI)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeManagementCI) this.offering).acceptProbed(peer, requestURI);

  }

}
