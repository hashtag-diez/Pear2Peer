package plugins.NetworkFacade;

import java.util.concurrent.RejectedExecutionException;

import components.interfaces.NodeManagementCI;
import interfaces.ContentNodeAddressI;
import interfaces.FacadeNodeAddressI;

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
public interface NodeManagementPI extends NodeManagementCI{
  /**
	 * Takes as a parameter the address of the peer node to be inserted into the
	 * network and returns a set of peer node addresses to which it can connect as
	 * new neighbor
	 * 
	 * @param a : address of the peer node which want to join the network.
	 * @return
	 * @throws Exception
	 */
	void join(ContentNodeAddressI a) throws Exception;

	/**
	 * Takes as a parameter the address of the peer node leaving the network.
	 * Components playing the role of peer nodes provide the NodeCI interface with
	 * the declared services next
	 * 
	 * @param a : address of the peer node which want to join the network.
	 * @throws Exception
	 */
	void leave(ContentNodeAddressI a) throws Exception;
  public void interconnect(FacadeNodeAddressI f) throws RejectedExecutionException, AssertionError, Exception;
}
