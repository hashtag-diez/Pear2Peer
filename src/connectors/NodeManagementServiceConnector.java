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
    System.out.println("Appel de Join du connecteur de " + a.getNodeIdentifier());
		return ((NodeManagementCI)this.offering).join(a);
  }
  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    System.out.println("Appel de Leave du connecteur de " + a.getNodeIdentifier());
		((NodeManagementCI)this.offering).leave(a);
  }
  
}
