package components;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import components.interfaces.ContentManagementCI;
import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import ports.NodeManagementInboundPort;
import ports.NodeManagementInboundPortCM;
import ports.NodeOutboundPortN;

@OfferedInterfaces(offered = { NodeManagementCI.class, ContentManagementCI.class })
@RequiredInterfaces(required = { ContentManagementCI.class })
public class NodeManagement
    extends AbstractComponent
    implements FacadeNodeAddressI {

  protected NodeManagementInboundPort NMSetterPort;
  protected NodeManagementInboundPortCM CMSetterPort;

  private Set<PeerNodeAddressI> members = new HashSet<>();
  protected String uriPrefix = "NodeC";

  protected NodeManagement(String reflectionInboundPortURI, String inboundURI) throws Exception {
    super(reflectionInboundPortURI, 1, 0);
    this.NMSetterPort = new NodeManagementInboundPort(inboundURI, this);
    this.NMSetterPort.publishPort();
    this.CMSetterPort = new NodeManagementInboundPortCM("cm"+inboundURI, this);
    this.CMSetterPort.publishPort();
    this.uriPrefix = this.uriPrefix + UUID.randomUUID();
  }

  public Set<PeerNodeAddressI> addNewComers(PeerNodeAddressI a) throws Exception {
    Set<PeerNodeAddressI> neighbors = new HashSet<>(members);
    members.add(a);
    return neighbors;
  }

  public void deletePeer(PeerNodeAddressI a) throws Exception {
    members.remove(a);
  }

  @Override
  public boolean isFacade() {
    return true;
  }

  @Override
  public boolean isPeer() {
    return false;
  }

  @Override
  public String getNodeIdentifier() throws Exception {
    return NMSetterPort.getPortURI();
  }

  @Override
  public String getNodeManagementURI() {
    return uriPrefix;
  }
  public ContentDescriptorI find(ContentTemplateI cd, int hops) {
		return null;
	}

	/** to review
	 */
	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops) {
		for (PeerNodeAddressI node : this.peersGetterPorts.keySet()) {
			NodeOutboundPortN outBoundPort = this.peersGetterPorts.get(node);
			matched.add()
		}
		return null;
	}
}
