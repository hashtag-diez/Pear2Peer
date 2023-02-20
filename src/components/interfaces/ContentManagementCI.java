package components.interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;

/**
 * The interface <code>ContentManagementCI</code> is offered and required by
 * both facades and node components to allow contents search.
 * 
 * @author ABSSI (Team)
 *
 */
public interface ContentManagementCI extends RequiredCI, OfferedCI {

	/**
	 * Takes as parameters a content pattern and a maximum number of authorized
	 * jumps between peers to return the descriptor of the content matching the
	 * pattern found by browsing the peers
	 * 
	 * @param cd   : the partial description of the content to find.
	 * @param hops : the maximum number of jumps between nodes.
	 * @return the complete description of the content or null if it not found
	 * @throws Exception
	 */
	ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception;

	/**
	 * Serch all in the network all the contents which match with the giving
	 * description.
	 * 
	 * @param cd : a partial content description.
	 * @param matched : a set of content descriptors already found
	 * @param hops : maximum number of jumps allowed between peers 
	 * @return a set of content complete descriptors matched.
	 * @throws Exception
	 */
	Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops) throws Exception;
}
