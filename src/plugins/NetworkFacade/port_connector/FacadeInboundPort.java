package plugins.NetworkFacade.port_connector;

import java.util.concurrent.RejectedExecutionException;

import plugins.NetworkFacade.NodeManagementPI;
import plugins.NetworkFacade.NodeManagementPlugin;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;

public class FacadeInboundPort
    extends AbstractInboundPort
    implements NodeManagementPI {

  public FacadeInboundPort(String pluginUri, ComponentI owner) throws Exception {
    super(NodeManagementPI.class, owner, pluginUri, null);
  }

  @Override
  public void join(PeerNodeAddressI a) throws Exception {
    this.getOwner().runTask(
      new AbstractComponent.AbstractTask(this.getPluginURI()) {
        @Override
        public void run() {
          try {
            ((NodeManagementPlugin) this.getTaskProviderReference()).join(a);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    this.getOwner().runTask(
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodeManagementPlugin) this.getTaskProviderReference()).leave(a);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
  }

  @Override
  public void acceptProbed(PeerNodeAddressI peer, String requestURI)
      throws RejectedExecutionException, AssertionError, Exception {
    this.getOwner().runTask(
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodeManagementPlugin) this.getTaskProviderReference()).acceptProbed(peer, requestURI);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
  }

  @Override
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops,PeerNodeAddressI chosen, int chosenNeighbourCount)
      throws RejectedExecutionException, AssertionError, Exception {
    this.getOwner().runTask(
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodeManagementPlugin) this.getTaskProviderReference()).probe(requestURI, facade, remainingHops,
                  chosen, chosenNeighbourCount);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
  }
}
