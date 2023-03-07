package plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import plugins.PluginOwnerI;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.ContentManagementPlugin;

/**
 * InboundPortCM
 */
public class CMInboundPort extends AbstractInboundPort
        implements ContentManagementPI {

    public CMInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, ContentManagementPI.class, owner);
    }

    @Override
    public void find(ContentTemplateI cd, int hops, String returnaddr) throws Exception {
        this.getOwner().runTask(
                owner -> {
                    try {
                        PluginOwnerI powner = (PluginOwnerI) owner;
                        ContentManagementPlugin plugin = (ContentManagementPlugin) powner
                                .getPlugin(PluginOwnerI.Plugins.ContentManagementPlugin);
                        plugin.find(cd, hops, returnaddr);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, String returnaddr)
            throws Exception {
        this.getOwner().runTask(
                owner -> {
                    try {
                        PluginOwnerI powner = (PluginOwnerI) owner;
                        ContentManagementPlugin plugin = (ContentManagementPlugin) powner
                                .getPlugin(PluginOwnerI.Plugins.ContentManagementPlugin);
                        plugin.match(cd, matched, hops, returnaddr);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
