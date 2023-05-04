package main.java.plugins.FacadeContentManagement;

import java.util.Set;

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
    void acceptFound(ContentDescriptorI found, String requestOwner) throws Exception;

    void acceptMatched(Set<ContentDescriptorI> found, String requestOwner) throws Exception;
}
