package plugins.NetworkNode.port_connector;


import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.PeerNodeAddressI;
import plugins.NetworkNode.NodePI;

public class NodeServiceConnector 
extends		AbstractConnector
implements	NodePI{
  @Override
  public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
    return ((NodePI)this.offering).connect(a);
  }
  @Override
  public void disconnect(PeerNodeAddressI a) throws Exception {
    ((NodePI)this.offering).disconnect(a);    
  }
  
}
