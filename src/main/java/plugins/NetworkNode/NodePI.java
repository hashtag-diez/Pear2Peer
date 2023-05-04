package main.java.plugins.NetworkNode;

import main.java.components.interfaces.NodeCI;
import main.java.interfaces.ContentNodeAddressI;

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
public interface NodePI extends NodeCI{
  void share(ContentNodeAddressI a) throws Exception;
}
