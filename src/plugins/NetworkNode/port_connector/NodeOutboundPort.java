package plugins.NetworkNode.port_connector;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.NetworkNode.NodePI;

public class NodeOutboundPort
    extends AbstractOutboundPort
    implements NodePI {
  public NodeOutboundPort(ComponentI owner) throws Exception {
    super(generatePortURI(), NodePI.class, owner);
  }

  @Override
  public void connect(PeerNodeAddressI a) throws Exception {
    ((NodePI) this.getConnector()).connect(a);
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    ((NodePI) this.getConnector()).disconnect(a);
  }

  @Override
  public void acceptNeighbours(Set<PeerNodeAddressI> neighbours)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodePI) this.getConnector()).acceptNeighbours(neighbours);
  }

  @Override
  public void acceptConnected(PeerNodeAddressI connected) throws RejectedExecutionException, AssertionError, Exception {
    ((NodePI) this.getConnector()).acceptConnected(connected);
  }

  @Override
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen, int chosenNeighbourCount)
      throws RejectedExecutionException, AssertionError, Exception {
    ((NodePI) this.getConnector()).probe(requestURI, facade, remainingHops, chosen, chosenNeighbourCount);
  }

}
