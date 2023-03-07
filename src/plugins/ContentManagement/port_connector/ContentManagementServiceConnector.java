package plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import plugins.ContentManagement.ContentManagementPI;

public class ContentManagementServiceConnector extends AbstractConnector
        implements ContentManagementPI {

    @Override
    public void find(ContentTemplateI cd, int hops, String returnAddr) throws Exception {
        ((ContentManagementPI) this.offering).find(cd, hops, returnAddr);
    }

    @Override
    public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, String returnAddr)
            throws Exception {
        ((ContentManagementPI) this.offering).match(cd, matched, hops, returnAddr);
    }

}
