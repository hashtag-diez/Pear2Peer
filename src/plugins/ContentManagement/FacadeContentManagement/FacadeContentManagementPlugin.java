package plugins.ContentManagement.FacadeContentManagement;

import java.util.Set;

import components.interfaces.ClientCI;
import connectors.ClientReturnConnector;
import fr.sorbonne_u.components.ComponentI;
import implem.ApplicationNode;
import interfaces.ContentDescriptorI;
import plugins.ContentManagement.ContentManagementPlugin;
import ports.ClientOutboundPort;
import interfaces.ContentManagementNodeAddressI;


public class FacadeContentManagementPlugin
    extends ContentManagementPlugin implements FacadeContentManagementPI {

  public FacadeContentManagementPlugin(String URI, int DescriptorId, ApplicationNode addr) throws Exception {
    super(URI, DescriptorId, (ContentManagementNodeAddressI) addr);
  }

  @Override
  public void installOn(ComponentI owner) throws Exception {
    super.installOn(owner);
    this.addOfferedInterface(FacadeContentManagementPI.class);
    this.addRequiredInterface(ClientCI.class);
  }

  private ClientOutboundPort makeClientOutboundPort(String clientUri) throws Exception {
    ClientOutboundPort clientOutboundPort = new ClientOutboundPort(this.getOwner());
    clientOutboundPort.publishPort();
    this.getOwner().doPortConnection(clientOutboundPort.getPortURI(), clientUri,
        ClientReturnConnector.class.getCanonicalName());
    return clientOutboundPort;
  }

  @Override
  public void acceptFound(ContentDescriptorI found, String requestOwner) throws Exception {
    makeClientOutboundPort(requestOwner).findResult(found);
  }

  @Override
  public void acceptMatched(Set<ContentDescriptorI> found, String requestOwner) throws Exception {
    makeClientOutboundPort(requestOwner).matchResult(found);
  }

}
