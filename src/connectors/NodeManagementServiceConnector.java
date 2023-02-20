package connectors;

import java.util.Set;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.PeerNodeAddressI;

public class NodeManagementServiceConnector 
extends		AbstractConnector
implements	NodeManagementCI{

  @Override
  public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
		return ((NodeManagementCI)this.offering).join(a);
  }
  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
		((NodeManagementCI)this.offering).leave(a);
  }
  
}
