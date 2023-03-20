package plugins.NetworkFacade.port_connector;

import java.util.Set;

import plugins.NetworkFacade.NodeManagementPI;
import plugins.NetworkFacade.NodeManagementPlugin;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.PeerNodeAddressI;

public class FacadeInboundPort
    extends AbstractInboundPort
    implements NodeManagementPI {

  public FacadeInboundPort(String pluginUri, ComponentI owner) throws Exception {
    super(NodeManagementPI.class, owner, pluginUri, null);
  }

  @Override
  public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
    return this.getOwner().handleRequest(
        new AbstractComponent.AbstractService<Set<PeerNodeAddressI>>(this.getPluginURI()) {
          @Override
          public Set<PeerNodeAddressI> call() throws Exception {
            return ((NodeManagementPlugin) this.getServiceProviderReference()).join(a);
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
}
