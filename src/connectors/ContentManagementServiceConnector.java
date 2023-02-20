package connectors;

import java.util.Set;

import components.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;

public class ContentManagementServiceConnector extends AbstractConnector
        implements ContentManagementCI {

    @Override
    public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception{
        return ((ContentManagementCI) this.offering).find(cd, hops);
    }

    @Override
    public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)  throws Exception{
        return ((ContentManagementCI) this.offering).match(cd, matched, hops);
    }

}
