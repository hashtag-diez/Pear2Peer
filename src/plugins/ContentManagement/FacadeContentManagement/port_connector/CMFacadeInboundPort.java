package plugins.ContentManagement.FacadeContentManagement.port_connector;

import java.util.Set;

import interfaces.ContentDescriptorI;
import plugins.ContentManagement.FacadeContentManagement.FacadeContentManagementPI;
import plugins.ContentManagement.FacadeContentManagement.FacadeContentManagementPlugin;
import plugins.ContentManagement.port_connector.CMInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;

/**
 * InboundPortCM
 */
public class CMFacadeInboundPort extends CMInboundPort
        implements FacadeContentManagementPI {

    public CMFacadeInboundPort(String pluginUri, ComponentI owner) throws Exception {
        super(pluginUri, owner, FacadeContentManagementPI.class);
    }

    @Override
    public void acceptFound(ContentDescriptorI found, String requestOwner) throws Exception {
        this.getOwner().runTask(
                new AbstractComponent.AbstractTask(this.getPluginURI()) {
                    @Override
                    public void run() {
                        try {
                            ((FacadeContentManagementPlugin) this.getTaskProviderReference()).acceptFound(found,
                                    requestOwner);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void acceptMatched(Set<ContentDescriptorI> found, String requestOwner) throws Exception {
        this.getOwner().runTask(
                new AbstractComponent.AbstractTask(this.getPluginURI()) {
                    @Override
                    public void run() {
                        try {
                            ((FacadeContentManagementPlugin) this.getTaskProviderReference()).acceptMatched(found,
                                    requestOwner);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
