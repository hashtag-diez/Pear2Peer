package plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ContentDescriptorI;
import interfaces.ContentManagementNodeAddressI;
import interfaces.ContentTemplateI;
import interfaces.ApplicationNodeAddressI;
import plugins.ContentManagement.ContentManagementPI;

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
