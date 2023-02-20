package ports;

import java.util.Set;

import components.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import components.NodeManagement;

/**
 * InboundPortCM
 */
public class NodeManagementInboundPortCM extends AbstractInboundPort
        implements ContentManagementCI {

    public NodeManagementInboundPortCM(String uri, ComponentI owner) throws Exception {
        super(uri, ContentManagementCI.class, owner);
    }

    @Override
    public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
        return this.getOwner().handleRequest(
                owner -> {
                    return ((NodeManagement) owner).find(cd, hops);
                });
    }

    @Override
    public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
            throws Exception {
        return this.getOwner().handleRequest(
                owner -> {
                    return ((NodeManagement) owner).match(cd, matched, hops);
                });
    }

}