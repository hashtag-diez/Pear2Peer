package connectors;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.NodeCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;

public class NodeServiceConnector
    extends AbstractConnector
    implements NodeCI {
  @Override
  public void connect(PeerNodeAddressI a) throws Exception {
    ((NodeCI) this.offering).connect(a);
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    ((NodeCI) this.offering).disconnect(a);
  }

  @Override
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI requester)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeCI) this.offering).probe(requestURI, facade, remainingHops, requester);
  }

  @Override
  public void acceptNeighbours(Set<PeerNodeAddressI> neighbours)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodeCI) this.offering).acceptNeighbours(neighbours);

  }

  @Override
  public void acceptConnected(PeerNodeAddressI connected) throws RejectedExecutionException, AssertionError, Exception {
    ((NodeCI) this.offering).acceptConnected(connected);
  }
}
