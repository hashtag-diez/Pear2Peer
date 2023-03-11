package components.interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.ContentDescriptorI;

public interface ClientCI extends OfferedCI, RequiredCI {

    /**
     * > This function is called when the search is complete
     * 
     * @param result The result of the search.
     */
    void findResult(ContentDescriptorI result) throws Exception;

    /**
     * > This function is called when the search is complete by a node
     * > (Can occur multiple times)
     * 
     * @param result The set of ContentDescriptorI objects that match the query.
     */
    void matchResult(Set<ContentDescriptorI> result) throws Exception;
}
