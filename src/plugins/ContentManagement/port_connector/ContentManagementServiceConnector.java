package plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.FacadeNodeAddressI;
import plugins.ContentManagement.ContentManagementPI;

public class ContentManagementServiceConnector extends AbstractConnector
        implements ContentManagementPI {

    @Override
    public void find(ContentTemplateI cd, int hops, FacadeNodeAddressI requester, String clientAddr) throws Exception {
        ((ContentManagementPI) this.offering).find(cd, hops, requester, clientAddr);
    }

    @Override
    public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, FacadeNodeAddressI requester,
            String clientAddr)
            throws Exception {
        ((ContentManagementPI) this.offering).match(cd, matched, hops, requester, clientAddr);
    }

}
