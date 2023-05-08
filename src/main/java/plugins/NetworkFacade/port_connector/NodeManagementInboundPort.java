package main.java.plugins.NetworkFacade.port_connector;

import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import main.java.implem.ApplicationNode;
import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;
import main.java.plugins.NetworkFacade.NodeManagementPI;
import main.java.plugins.NetworkFacade.NodeManagementPlugin;

public class NodeManagementInboundPort
    extends AbstractInboundPort
    implements NodeManagementPI {

  public NodeManagementInboundPort(String uri, String pluginUri, ComponentI owner, String executorServiceURI) throws Exception {
    super(uri, NodeManagementPI.class, owner, pluginUri, executorServiceURI);
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
  public void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
      int chosenNeighbourCount)
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

  @Override
  public void interconnect(ApplicationNode f) throws RejectedExecutionException, AssertionError, Exception {
    this.getOwner().runTask(
        new AbstractComponent.AbstractTask(this.getPluginURI()) {
          @Override
          public void run() {
            try {
              ((NodeManagementPlugin) this.getTaskProviderReference()).interconnect(f);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
  }
}
