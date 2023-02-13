package components.interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.PeerNodeAddressI;
public interface NodeManagementCI 
extends OfferedCI, RequiredCI {

  Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception;
  void leave(PeerNodeAddressI a) throws Exception;
}
