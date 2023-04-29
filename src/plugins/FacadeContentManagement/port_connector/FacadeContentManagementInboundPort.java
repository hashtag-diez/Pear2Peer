package plugins.FacadeContentManagement.port_connector;

import java.util.Set;

import interfaces.ContentDescriptorI;
import plugins.ContentManagement.port_connector.ContentManagementInboundPort;
import plugins.FacadeContentManagement.FacadeContentManagementPI;
import plugins.FacadeContentManagement.FacadeContentManagementPlugin;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;

/**
 * InboundPortCM
 */
public class FacadeContentManagementInboundPort extends ContentManagementInboundPort
        implements FacadeContentManagementPI {

    public FacadeContentManagementInboundPort(String uri, String pluginUri, ComponentI owner, String executorServiceURI) throws Exception {
        super(uri,  pluginUri, owner, FacadeContentManagementPI.class, executorServiceURI);
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
