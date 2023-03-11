package plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.ContentManagementPlugin;

/**
 * InboundPortCM
 */
public class CMInboundPort extends AbstractInboundPort
        implements ContentManagementPI {

    public CMInboundPort(String pluginUri, ComponentI owner) throws Exception {
        super(ContentManagementPI.class, owner, pluginUri, null);
    }

    @Override
    public void find(ContentTemplateI cd, int hops, String returnaddr) throws Exception {
        this.getOwner().runTask(
                new AbstractComponent.AbstractTask(this.getPluginURI()) {
                    @Override
                    public void run() {
                        try {
                            ((ContentManagementPlugin) this.getTaskProviderReference()).find(cd, hops, returnaddr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, String returnaddr)
            throws Exception {
        this.getOwner().runTask(
                new AbstractComponent.AbstractTask(this.getPluginURI()) {
                    @Override
                    public void run() {
                        try {
                            ((ContentManagementPlugin) this.getTaskProviderReference()).match(cd, matched, hops, returnaddr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
