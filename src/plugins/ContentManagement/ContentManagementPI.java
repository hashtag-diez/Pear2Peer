package plugins.ContentManagement;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.FacadeNodeAddressI;

/**
 * The interface <code>ContentManagementCI</code> is offered and required by
 * both facades and node components to allow contents search.
 * 
 * @author ABSSI (Team)
 *
 */
public interface ContentManagementPI extends RequiredCI, OfferedCI {

	void find(ContentTemplateI cd, int hops, FacadeNodeAddressI requester, String clientAddr) throws Exception;

	void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, FacadeNodeAddressI requester,
			String clientAddr)
			throws Exception;
}
