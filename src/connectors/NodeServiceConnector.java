package connectors;


import components.interfaces.NodeCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.PeerNodeAddressI;

public class NodeServiceConnector 
extends		AbstractConnector
implements	NodeCI{
  @Override
  public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
    return ((NodeCI)this.offering).connect(a);
  }
  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    ((NodeCI)this.offering).disconnect(a);    
  }
  
}
