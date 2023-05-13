package main.java.components.interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import main.java.interfaces.ContentDescriptorI;

public interface ClientCI extends OfferedCI, RequiredCI {

    /**
     * > This function is called when the find is complete by a node
     * > Due to a boolean flag, this function can only be called once
     * 
     * @param result The set of ContentDescriptorI objects that match the query.
     */
    void findResult(ContentDescriptorI result, String URI) throws Exception;

    /**
     * > This function is called when the match is complete by a node
     * > (Can occur multiple times)
     * 
     * @param result The set of ContentDescriptorI objects that match the query.
     */
    void matchResult(Set<ContentDescriptorI> result, String URI) throws Exception;
}
