package plugins.NetworkNode.port_connector;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.NetworkNode.NodePI;
import plugins.NetworkNode.NodePlugin;

public class NodeInboundPort
    extends AbstractInboundPort
    implements NodePI {

  public NodeInboundPort(String pluginUri, ComponentI owner, String executorServiceURI) throws Exception {
    super(NodePI.class, owner, pluginUri, executorServiceURI);
  }

  @Override
  public void connect(PeerNodeAddressI a) throws Exception {
    this.getOwner().runTask(
        // pool
        this.getExecutorServiceURI(),
        // task
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodePlugin) this.getTaskProviderReference()).connect(a);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        });
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    this.getOwner().runTask(
        // pool
        this.getExecutorServiceURI(),
        // task
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodePlugin) this.getTaskProviderReference()).disconnect(a);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        });
  }

  @Override
  public void acceptNeighbours(Set<PeerNodeAddressI> neighbours)
      throws RejectedExecutionException, AssertionError, Exception {
    this.getOwner().runTask(
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodePlugin) this.getTaskProviderReference()).acceptNeighbours(neighbours);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        });
  }

  @Override
  public void acceptConnected(PeerNodeAddressI connected) throws RejectedExecutionException, AssertionError, Exception {
    this.getOwner().runTask(
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodePlugin) this.getTaskProviderReference()).acceptConnected(connected);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        });
  }

  @Override
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen, int chosenNeighbourCount)
      throws RejectedExecutionException, AssertionError, Exception {
    this.getOwner().runTask(
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodePlugin) this.getTaskProviderReference()).probe(requestURI, facade, remainingHops, chosen, chosenNeighbourCount);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        });
  }

}
