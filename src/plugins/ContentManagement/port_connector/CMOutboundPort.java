package plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.FacadeNodeAddressI;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.FacadeContentManagement.FacadeContentManagementPI;

public class CMOutboundPort
    extends AbstractOutboundPort
    implements ContentManagementPI {

  public CMOutboundPort(ComponentI owner, Class<FacadeContentManagementPI> clasz) throws Exception {
    super(generatePortURI(),clasz, owner);
  }
  public CMOutboundPort(ComponentI owner) throws Exception {
    super(generatePortURI(),ContentManagementPI.class, owner);
  }
  @Override
  public void find(ContentTemplateI cd, int hops, FacadeNodeAddressI requester, String clientAddr) throws Exception {
    ((ContentManagementPI) this.getConnector()).find(cd, hops, requester, clientAddr);
  }

  @Override
  public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, FacadeNodeAddressI requester,
      String clientAddr)
      throws Exception {
    ((ContentManagementPI) this.getConnector()).match(cd, matched, hops, requester, clientAddr);
  }
}