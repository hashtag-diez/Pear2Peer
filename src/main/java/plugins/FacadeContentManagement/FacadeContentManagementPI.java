package main.java.plugins.FacadeContentManagement;

import java.util.Set;

import main.java.implem.ApplicationNode;
import main.java.interfaces.ContentDescriptorI;
import main.java.plugins.ContentManagement.ContentManagementPI;

/**
 * The interface <code>ContentManagementCI</code> is offered and required by
 * both facades and node components to allow contents search.
 * 
 * @author ABSSI (Team)
 *
 */
public interface FacadeContentManagementPI extends ContentManagementPI {
    /**
     * This function accepts a content descriptor and a request owner as parameters
     * 
     * @param found        Contains information about some
     *                     content that has been
     *                     found or retrieved.
     * @param requestOwner
     *                     Represents the owner
     *                     of the request for the content descriptor. It is used as
     *                     a reference to identify who made the
     *                     request for the content descriptor.
     */
    void acceptFound(ContentDescriptorI found, String requestOwner) throws Exception;

    /**
     * This function accepts a set of matched content descriptors and a request
     * owner as parameters and
     * throws an exception.
     * 
     * @param found        Found is a Set of ContentDescriptorI objects that
     *                     represent the content that has
     *                     been matched.
     * @param requestOwner Represents the owner
     *                     of the request for the content descriptor. It is used as
     *                     a reference to identify who made the
     *                     request for the content descriptor.
     * @throws Exception
     */
    void acceptMatched(Set<ContentDescriptorI> found, String requestOwner) throws Exception;

    void acceptShared(ApplicationNode connected) throws Exception;
}
