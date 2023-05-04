package main.java.plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import main.java.interfaces.ApplicationNodeAddressI;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentManagementNodeAddressI;
import main.java.interfaces.ContentTemplateI;
import main.java.plugins.ContentManagement.ContentManagementPI;

public class ContentManagementServiceConnector extends AbstractConnector
        implements ContentManagementPI {

    @Override
    public void find(ContentTemplateI cd, int hops, ApplicationNodeAddressI requester, String clientAddr) throws Exception {
        ((ContentManagementPI) this.offering).find(cd, hops, requester, clientAddr);
    }

    @Override
    public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, ApplicationNodeAddressI requester,
            String clientAddr)
            throws Exception {
        ((ContentManagementPI) this.offering).match(cd, matched, hops, requester, clientAddr);
    }

    @Override
    public void acceptShared(ContentManagementNodeAddressI connected) throws Exception {
      ((ContentManagementPI) this.offering).acceptShared(connected);
    }
}
