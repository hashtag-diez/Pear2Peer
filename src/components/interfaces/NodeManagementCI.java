package components.interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.PeerNodeAddressI;

/**
 * The interface <code>NodeManagementCI</code> represents an interface which is
 * offered by facade components and required by node components.
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * It most general way, it allows to a node to join or leave the network.
 * </p>
 * 
 * @author ABSSI (Team)
 *
 */
public interface NodeManagementCI extends OfferedCI, RequiredCI {

	/**
	 * Takes as a parameter the address of the peer node to be inserted into the
	 * network and returns a set of peer node addresses to which it can connect as
	 * new neighbor
	 * 
	 * @param a : address of the peer node which want to join the network.
	 * @return
	 * @throws Exception
	 */
	Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception;

	/**
	 * Takes as a parameter the address of the peer node leaving the network.
	 * Components playing the role of peer nodes provide the NodeCI interface with
	 * the declared services next
	 * 
	 * @param a : address of the peer node which want to join the network.
	 * @throws Exception
	 */
	void leave(PeerNodeAddressI a) throws Exception;
}
