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
    return ((NodeManagementCI) this.getConnector()).join(a);

  }

  @Override
  public void leave(PeerNodeAddressI a) throws Exception {
    ((NodeManagementCI) this.getConnector()).leave(a);
  }

  @Override
  public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
    return ((ContentManagementCI) this.getConnector()).find(cd, hops) ; 
  }

  @Override
  public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops) {
    // TODO Auto-generated method stub
    return null;
  }
}
