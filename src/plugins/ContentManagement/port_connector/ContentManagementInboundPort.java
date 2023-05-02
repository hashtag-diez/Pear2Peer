package plugins.ContentManagement.port_connector;

import java.util.Set;

import org.apache.commons.math3.ml.neuralnet.twod.util.HitHistogram;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentManagementNodeAddressI;
import interfaces.ContentTemplateI;
import interfaces.ApplicationNodeAddressI;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.FacadeContentManagement.FacadeContentManagementPI;
import utiles.DebugDisplayer;

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
