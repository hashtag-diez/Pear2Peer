package plugins.NetworkNode.port_connector;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.PeerNodeAddressI;
import plugins.NetworkNode.NodePI;
import plugins.NetworkNode.NodePlugin;

public class NodeInboundPort
    extends AbstractInboundPort
    implements NodePI {


  public NodeInboundPort(String pluginUri, ComponentI owner) throws Exception {
    super(NodePI.class, owner, pluginUri,null);
  }

  @Override
  public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
    return this.getOwner().handleRequest(
        new AbstractComponent.AbstractService<PeerNodeAddressI>(this.getPluginURI()) {
          @Override
          public PeerNodeAddressI call() throws Exception {
            return ((NodePlugin) this.getServiceProviderReference()).connect(a);
          }
        });
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    this.getOwner().runTask(
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

}
