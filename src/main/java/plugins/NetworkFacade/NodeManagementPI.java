package main.java.plugins.NetworkFacade;

import main.java.components.interfaces.NodeManagementCI;
import main.java.implem.ApplicationNode;
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
public interface NodeManagementPI extends NodeManagementCI {
	/**
	 * Takes as a parameter the address of the peer node to be inserted into the
	 * network and returns a set of peer node addresses to which it can connect as
	 * new neighbor
	 * 
	 * @param a : address of the peer node which want to join the network.
	 * @throws Exception
	 */
	void join(PeerNodeAddressI a) throws Exception;

	/**
	 * Takes as a parameter the address of the peer node leaving the network.
	 * Components playing the role of peer nodes provide the NodeCI interface with
	 * the declared services next
	 * 
	 * @param a : address of the peer node which want to join the network.
	 * @throws Exception
	 */
	void leave(PeerNodeAddressI a) throws Exception;

	public void interconnect(ApplicationNode f) throws Exception;
}
