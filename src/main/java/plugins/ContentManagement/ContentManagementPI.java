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

	void find(ContentTemplateI cd, int hops, ApplicationNodeAddressI requester, String clientAddr) throws Exception;

	void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, ApplicationNodeAddressI requester,
			String clientAddr)
			throws Exception;
	void acceptShared(ContentManagementNodeAddressI connected) throws Exception;
}
