package main.java.plugins.ContentManagement;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import main.java.interfaces.ApplicationNodeAddressI;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentManagementNodeAddressI;
import main.java.interfaces.ContentTemplateI;

/**
 * The interface <code>ContentManagementCI</code> is offered and required by
 * both facades and node components to allow contents search.
 * 
 * @author ABSSI (Team)
 *
 */
public interface ContentManagementPI extends RequiredCI, OfferedCI {

	/**
	 * This Java function finds a content template with a specified number of hops,
	 * requester, and client address.
	 * 
	 * @param cd         Contains information about the content that is being
	 *                   searched for or requested.
	 * @param hops       The hops that can be made while searching for the requested
	 *                   content. It is used to limit the scope of
	 *                   the search and prevent excessive network traffic.
	 * @param requester  The parameter requester address of the node that is
	 *                   requesting the "find" operation.
	 * @param clientAddr The parameter "clientAddr" is a String that represents the
	 *                   address of the client
	 *                   that is requesting the "find" operation.
	 */
	void find(ContentTemplateI cd, int hops, ApplicationNodeAddressI requester, String clientAddr) throws Exception;

	/**
	 * This Java function matches a content template with a specified number of
	 * hops,
	 * 
	 * @param cd         Contains information about the content that is being
	 * 
	 * @param matched    The set of content descriptors that match the content
	 * 
	 * @param hops       The hops that can be made while searching for the requested
	 * 
	 * @param requester  The parameter requester address of the node that is
	 *                   requesting the "match" operation.
	 * 
	 * @param clientAddr The parameter "clientAddr" is a String that represents the
	 *                   address of the client
	 *                   that is requesting the "match" operation.
	 */
	void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, ApplicationNodeAddressI requester,
			String clientAddr)
			throws Exception;

	void acceptShared(ContentManagementNodeAddressI connected) throws Exception;
}
