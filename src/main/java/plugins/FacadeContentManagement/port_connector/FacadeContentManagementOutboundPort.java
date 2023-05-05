package main.java.plugins.FacadeContentManagement.port_connector;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import main.java.implem.ApplicationNode;
import main.java.interfaces.ContentDescriptorI;
import main.java.plugins.ContentManagement.port_connector.ContentManagementOutboundPort;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPI;

public class FacadeContentManagementOutboundPort
    extends ContentManagementOutboundPort
    implements FacadeContentManagementPI {

  public FacadeContentManagementOutboundPort(ComponentI owner) throws Exception {
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

  @Override
  public void acceptShared(ApplicationNode connected) throws Exception {
    ((FacadeContentManagementPI) this.getConnector()).acceptShared(connected);
  }
}