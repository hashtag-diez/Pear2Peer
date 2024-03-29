package main.java.connectors;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import main.java.interfaces.ContentNodeAddressI;
import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;
import main.java.plugins.NetworkNode.NodePI;

public class NodeServiceConnector
    extends AbstractConnector
    implements NodePI {
  @Override
  public void connect(PeerNodeAddressI a) throws Exception {
    ((NodePI) this.offering).connect(a);
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    ((NodePI) this.offering).disconnect(a);
  }

  @Override
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
      int chosenNeighbourCount)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodePI) this.offering).probe(requestURI, facade, remainingHops, chosen, chosenNeighbourCount);
  }

  @Override
  public void acceptNeighbours(Set<PeerNodeAddressI> neighbours)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodePI) this.offering).acceptNeighbours(neighbours);

  }

  @Override
  public void acceptConnected(PeerNodeAddressI connected) throws RejectedExecutionException, AssertionError, Exception {
    ((NodePI) this.offering).acceptConnected(connected);
  }

  @Override
  public void share(ContentNodeAddressI a) throws Exception {
    ((NodePI) this.offering).share(a);

  }
}
