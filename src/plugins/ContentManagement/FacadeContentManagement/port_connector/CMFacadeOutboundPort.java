package plugins.ContentManagement.FacadeContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import interfaces.ContentDescriptorI;
import plugins.ContentManagement.FacadeContentManagement.FacadeContentManagementPI;
import plugins.ContentManagement.port_connector.CMOutboundPort;

public class CMFacadeOutboundPort
    extends CMOutboundPort
    implements FacadeContentManagementPI {

  public CMFacadeOutboundPort(ComponentI owner) throws Exception {
    super(owner, FacadeContentManagementPI.class);
  }

  @Override
  public void acceptFound(ContentDescriptorI found, String requestOwner) throws Exception {
    ((FacadeContentManagementPI) this.getConnector()).acceptFound(found, requestOwner);
  }

  @Override
  public void acceptMatched(Set<ContentDescriptorI> found, String requestOwner) throws Exception {
    ((FacadeContentManagementPI) this.getConnector()).acceptMatched(found, requestOwner);
  }
}