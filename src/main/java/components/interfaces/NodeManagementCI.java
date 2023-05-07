package main.java.components.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import main.java.interfaces.PeerNodeAddressI;

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
public interface NodeManagementCI extends OfferedCI, RequiredCI, ProbingCI {

	/**
	 * Takes as a parameter the address of the peer node to be inserted into the
	 * network and start a probing
	 * 
	 * @param a : address of the peer node which want to join the network.
	 * @return
	 * @throws Exception
	 */
	void join(PeerNodeAddressI a) throws Exception;

	/**
	 * Takes as a parameter the address of the peer node leaving the network.
	 * If its a root node, it will destroy the connection between the two.
	 * 
	 * @param a : address of the peer node which want to leave the network.
	 * @throws Exception
	 */
	void leave(PeerNodeAddressI a) throws Exception;

	/**
	 * Collect one the probing request and process it.
	 *
	 * @param peer       : address of the peer node which accept the probing
	 *                   request.
	 * @param requestURI : URI of the requesting node.
	 * @throws Exception
	 */
	void acceptProbed(PeerNodeAddressI peer, String requestURI)
			throws Exception;
}
