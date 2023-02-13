package ports;

import java.util.Set;

import components.interfaces.NodeCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.PeerNodeAddressI;

public class NodeOutboundPortN 
extends		AbstractOutboundPort
implements NodeCI{
  public NodeOutboundPortN(String uri, ComponentI owner) throws Exception {
    super(uri, NodeCI.class, owner);
  }
  @Override
  public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
    return ((NodeCI)this.getConnector()).connect(a) ;
  }

  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    // TODO Auto-generated method stub
    
  }
}
