package components.interfaces;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.PeerNodeAddressI;

/**
 * The interface <code>NodeCI</code> is offered and required by both facade
 * components and node components, in general way by pear components.
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Its allows to connect or disconnect two nodes.
 * </p>
 * 
 * @author ABSSI (Team)
 *
 */
public interface NodeCI extends OfferedCI, RequiredCI, ProbingCI {

	/**
	 * receives as a parameter the address of a new peer that wishes to connect to
	 * it by as a neighbor, connects to it and returns its own address as
	 * confirmation.
	 * 
	 * @param a : address of a the peer node to connect with.
	 * @return the address of the current node
	 * @throws Exception
	 */
	void connect(PeerNodeAddressI a) throws Exception;

	/**
	 * receive the address of a neighbor and disconnects from it.
	 * 
	 * @param a : address of the peer node to disconnect from.
	 * @throws Exception
	 */
	void disconnect(PeerNodeAddressI a) throws Exception;

	void acceptNeighbours(Set<PeerNodeAddressI> neighbours) throws RejectedExecutionException, AssertionError, Exception;
	void acceptConnected(PeerNodeAddressI connected) throws RejectedExecutionException, AssertionError, Exception;
	
}
