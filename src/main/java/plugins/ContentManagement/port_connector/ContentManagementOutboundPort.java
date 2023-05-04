package main.java.plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import main.java.interfaces.ApplicationNodeAddressI;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentManagementNodeAddressI;
import main.java.interfaces.ContentTemplateI;
import main.java.plugins.ContentManagement.ContentManagementPI;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPI;

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