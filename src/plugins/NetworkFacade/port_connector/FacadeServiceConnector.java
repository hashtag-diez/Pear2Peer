package plugins.NetworkFacade.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.PeerNodeAddressI;
import plugins.NetworkFacade.NodeManagementPI;

public class FacadeServiceConnector 
extends		AbstractConnector
implements	NodeManagementPI{

  @Override
  public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
		return ((NodeManagementPI)this.offering).join(a);
  }
  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
		((NodeManagementPI)this.offering).leave(a);
  }
  
}
