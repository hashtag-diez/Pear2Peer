package main.java.plugins.FacadeContentManagement.port_connector;

import java.util.Set;

import main.java.implem.ApplicationNode;
import main.java.interfaces.ContentDescriptorI;
import main.java.plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPI;

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

    @Override
    public void acceptShared(ApplicationNode connected) throws Exception {
        ((FacadeContentManagementPI) this.offering).acceptShared(connected);
    }

}
