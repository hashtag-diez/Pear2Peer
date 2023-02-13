package ports;

import java.util.Set;

import components.interfaces.NodeManagementCI;
import components.NodeManagement;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.PeerNodeAddressI;

public class NodeManagementInboundPort 
extends		AbstractInboundPort
implements NodeManagementCI{

  public NodeManagementInboundPort(String uri, ComponentI owner) throws Exception {
    super(uri, NodeManagementCI.class, owner);
  }

  @Override
  public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception{
    return this.getOwner().handleRequest(
      owner -> ((NodeManagement)owner).addNewComers(a));
  }

  @Override
  public void leave(PeerNodeAddressI a) {
    
  }
  
}
