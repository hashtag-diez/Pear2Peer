package plugins.FacadeContentManagement.port_connector;

import java.util.Set;

import interfaces.ContentDescriptorI;
import plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import plugins.FacadeContentManagement.FacadeContentManagementPI;

public class FacadeContentManagementServiceConnector extends ContentManagementServiceConnector
        implements FacadeContentManagementPI {

    @Override
    public void acceptFound(ContentDescriptorI found, String requestOwner) throws Exception {
        ((FacadeContentManagementPI) this.offering).acceptFound(found, requestOwner);
    }

    @Override
    public void acceptMatched(Set<ContentDescriptorI> found, String requestOwner) throws Exception {
        ((FacadeContentManagementPI) this.offering).acceptMatched(found, requestOwner);
    }

}
