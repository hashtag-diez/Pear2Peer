package components.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.PeerNodeAddressI;

public interface NodeCI 
extends OfferedCI, RequiredCI{
  PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception;
  void disconnect(PeerNodeAddressI a) throws Exception;
}
