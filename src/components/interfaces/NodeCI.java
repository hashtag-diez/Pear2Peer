package components.interfaces;

import java.util.Set;

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
	 * The connect function establishes a connection with a peer node address.
	 * 
	 * @param a The parameter a is the address of the peer node to connect to.
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

	/**
	 * This function is called after a sucessful probing. It run connect on all the
	 * neighbours received
	 * 
	 * @param neighbours : set of neighbours to connect to.
	 * @throws Exception
	 */
	void acceptNeighbours(Set<PeerNodeAddressI> neighbours)
			throws Exception;

	void acceptConnected(PeerNodeAddressI connected) throws Exception;

}
