package plugins.NetworkFacade;

import java.util.concurrent.RejectedExecutionException;

import components.interfaces.NodeManagementCI;
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
  public void interconnect(FacadeNodeAddressI f) throws RejectedExecutionException, AssertionError, Exception;
}
