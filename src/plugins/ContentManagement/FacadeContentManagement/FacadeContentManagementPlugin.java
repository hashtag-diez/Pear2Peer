package plugins.ContentManagement.FacadeContentManagement;

import java.util.Set;

import connectors.ClientReturnConnector;
import interfaces.ContentDescriptorI;
import interfaces.ContentNodeAddressI;
import plugins.ContentManagement.ContentManagementPlugin;
import ports.ClientOutboundPort;

public class FacadeContentManagementPlugin
    extends ContentManagementPlugin implements FacadeContentManagementPI {

  public FacadeContentManagementPlugin(int DescriptorId, ContentNodeAddressI addr) throws Exception {
    super(DescriptorId, addr);
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
