package ports;

import java.util.Set;

import components.interfaces.ContentManagementCI;
import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.PeerNodeAddressI;

public class NodeOutboundPortNM
    extends AbstractOutboundPort
    implements NodeManagementCI, ContentManagementCI {
  public NodeOutboundPortNM(String uri, ComponentI owner) throws Exception {
    super(uri, NodeManagementCI.class, owner);
  }

  @Override
  public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
    System.out.println("Appel de Join de l'outbound de " + a.getNodeIdentifier());
    return ((NodeManagementCI) this.getConnector()).join(a);

  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    System.out.println("Appel de Leave de l'outbound de " + a.getNodeIdentifier());
    ((NodeManagementCI) this.getConnector()).leave(a);
  }

  @Override
  public ContentDescriptorI find(ContentTemplateI cd, int hops) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops) {
    // TODO Auto-generated method stub
    return null;
  }
}
