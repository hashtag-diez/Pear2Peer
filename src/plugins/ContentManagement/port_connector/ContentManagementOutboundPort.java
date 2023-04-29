package plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentManagementNodeAddressI;
import interfaces.ContentTemplateI;
import interfaces.ApplicationNodeAddressI;
import plugins.ContentManagement.ContentManagementPI;
import plugins.FacadeContentManagement.FacadeContentManagementPI;

public class ContentManagementOutboundPort
    extends AbstractOutboundPort
    implements ContentManagementPI {

  public ContentManagementOutboundPort(ComponentI owner, Class<FacadeContentManagementPI> clasz) throws Exception {
    super(generatePortURI(),clasz, owner);
  }
  public ContentManagementOutboundPort(ComponentI owner) throws Exception {
    super(generatePortURI(),ContentManagementPI.class, owner);
  }
  @Override
  public void find(ContentTemplateI cd, int hops, ApplicationNodeAddressI requester, String clientAddr) throws Exception {
    ((ContentManagementPI) this.getConnector()).find(cd, hops, requester, clientAddr);
  }

  @Override
  public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, ApplicationNodeAddressI requester,
      String clientAddr)
      throws Exception {
    ((ContentManagementPI) this.getConnector()).match(cd, matched, hops, requester, clientAddr);
  }
  @Override
  public void acceptShared(ContentManagementNodeAddressI connected) throws Exception {
    ((ContentManagementPI) this.getConnector()).acceptShared(connected);
  }
}