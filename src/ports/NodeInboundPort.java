package ports;

import components.Node;
import components.interfaces.NodeCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.PeerNodeAddressI;

public class NodeInboundPort 
extends		AbstractInboundPort
implements NodeCI{

  public NodeInboundPort(String uri, ComponentI owner) throws Exception {
    super(AbstractInboundPort.generatePortURI(),NodeCI.class, owner);
  }

  @Override
  public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
    return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<PeerNodeAddressI>() {
					@Override
					public PeerNodeAddressI call() throws Exception {
						return ((Node)this.getServiceOwner()).addToNetwork(a);
					}
				});
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception{
    System.out.println("Appel de Leave du inbound de " + a.getNodeIdentifier());
    this.getOwner().runTask(
				owner -> {
          try {
            ((Node) owner).deleteFromNetwork(a);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
    );
  }
}
