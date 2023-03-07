package ports;

import java.util.Set;

import components.interfaces.ContentManagementCI;
import plugins.ContentManagementPlugin;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;

/**
 * InboundPortCM
 */
public class InboundPortCM extends AbstractInboundPort
        implements ContentManagementCI {

    public InboundPortCM(String pluginURI,
            ComponentI owner) throws Exception {
        super(ContentManagementCI.class, owner, pluginURI, null);
    }

    @Override
    public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
        return this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<ContentDescriptorI>(this.getPluginURI()) {
                    @Override
                    public ContentDescriptorI call() throws Exception {
                        return ((ContentManagementPlugin) this.getServiceProviderReference()).find(cd, hops);
                    }
                });
    }

    @Override
    public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
            throws Exception {
        return this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<Set<ContentDescriptorI>>(this.getPluginURI()) {
                    @Override
                    public Set<ContentDescriptorI> call() throws Exception {
                        return ((ContentManagementPlugin) this.getServiceProviderReference()).match(cd, matched, hops);

                    }
                });
    }
}
