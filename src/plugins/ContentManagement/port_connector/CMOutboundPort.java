package plugins.ContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import plugins.ContentManagement.ContentManagementPI;

public class CMOutboundPort
    extends AbstractOutboundPort
    implements ContentManagementPI {

  public CMOutboundPort(ComponentI owner) throws Exception {
    super(generatePortURI(), ContentManagementPI.class, owner);
  }

  @Override
  public void find(ContentTemplateI cd, int hops, String returnAddr) throws Exception {
    ((ContentManagementPI) this.getConnector()).find(cd, hops, returnAddr);
  }

  @Override
  public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops,
      String returnAddr)
      throws Exception {
    ((ContentManagementPI) this.getConnector()).match(cd, matched, hops, returnAddr);
  }
}