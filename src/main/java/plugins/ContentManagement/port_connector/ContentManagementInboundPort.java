package main.java.plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import main.java.interfaces.ApplicationNodeAddressI;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentManagementNodeAddressI;
import main.java.interfaces.ContentTemplateI;
import main.java.plugins.ContentManagement.ContentManagementPI;
import main.java.plugins.ContentManagement.ContentManagementPlugin;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPI;

/**
 * InboundPortCM
 */
public class ContentManagementInboundPort extends AbstractInboundPort
        implements ContentManagementPI {

    public ContentManagementInboundPort(String uri, String pluginUri, ComponentI owner, String executorServiceURI) throws Exception {
        super(uri, ContentManagementPI.class, owner, pluginUri, executorServiceURI);
    }

    public ContentManagementInboundPort(String uri, String pluginUri, ComponentI owner, Class<FacadeContentManagementPI> class1, String executorServiceURI)
            throws Exception {
        super(uri, class1, owner, pluginUri, executorServiceURI);
    }

    @Override
    public void find(ContentTemplateI cd, int hops, ApplicationNodeAddressI requester, String clientAddr) throws Exception {
        this.getOwner().runTask(
                // service executor vise
                this.getExecutorServiceURI(),
                // tache à executer
                new AbstractComponent.AbstractTask(this.getPluginURI()) {
                    @Override
                    public void run() {
                        try {
                            ((ContentManagementPlugin) this.getTaskProviderReference()).find(cd, hops, requester,
                                    clientAddr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, ApplicationNodeAddressI requester,
            String clientAddr)
            throws Exception {
        this.getOwner().runTask(
                // service executor vise
                this.getExecutorServiceURI(),
                // tache à executer
                new AbstractComponent.AbstractTask(this.getPluginURI()) {
                    @Override
                    public void run() {
                        try {
                            ((ContentManagementPlugin) this.getTaskProviderReference()).match(cd, matched, hops,
                                    requester,
                                    clientAddr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void acceptShared(ContentManagementNodeAddressI connected) throws Exception {
        this.getOwner().runTask(
            // service executor vise
            this.getExecutorServiceURI(),
            // tache à executer
            new AbstractComponent.AbstractTask(this.getPluginURI()) {
                @Override
                public void run() {
                    try {
                        ((ContentManagementPlugin) this.getTaskProviderReference()).acceptShared(connected);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }
}
