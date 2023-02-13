package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.sorbonne_u.utils.Pair;

import components.interfaces.ContentManagementCI;
import components.interfaces.NodeManagementCI;
import connectors.ContentManagementServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import ports.NodeManagementInboundPort;
import ports.NodeManagementInboundPortCM;
import ports.OutboundPortCM;

@OfferedInterfaces(offered = { NodeManagementCI.class, ContentManagementCI.class })
@RequiredInterfaces(required = { ContentManagementCI.class })
public class NodeManagement
    extends AbstractComponent
    implements FacadeNodeAddressI {

  protected List<ContentDescriptorI> contentsDescriptors;
  protected NodeManagementInboundPort NMSetterPort;
  protected NodeManagementInboundPortCM CMSetterPort;

  private HashMap<PeerNodeAddressI, OutboundPortCM> members = new HashMap<>();
  protected String uriPrefix = "NodeC";

  protected NodeManagement(String reflectionInboundPortURI, String inboundURI) throws Exception {
    super(reflectionInboundPortURI, 1, 0);
    this.NMSetterPort = new NodeManagementInboundPort(inboundURI, this);
    this.NMSetterPort.publishPort();
    this.CMSetterPort = new NodeManagementInboundPortCM("cm"+inboundURI, this);
    this.CMSetterPort.publishPort();
    this.contentsDescriptors = new ArrayList<>();
    this.uriPrefix = this.uriPrefix + UUID.randomUUID();
  }

  public Set<PeerNodeAddressI> addNewComers(PeerNodeAddressI a) throws Exception {
    HashMap<PeerNodeAddressI, OutboundPortCM> neighbors = (HashMap<PeerNodeAddressI, OutboundPortCM>) members.clone();
    
    String oportCM = AbstractOutboundPort.generatePortURI();
    OutboundPortCM peerOutPortCM = new OutboundPortCM(oportCM, this);
		peerOutPortCM.publishPort();
    this.doPortConnection(oportCM, CMSetterPort.getPortURI(), ContentManagementServiceConnector.class.getCanonicalName());

    members.put(a,peerOutPortCM);
    return neighbors
      .keySet()
      .stream()
      .skip(neighbors.size() > 3 ? neighbors.size() - 3 : 0)
      .limit(3)
      .collect(Collectors.toSet());
  }

  public void deletePeer(PeerNodeAddressI a) throws Exception {
    OutboundPortCM peerPortCM = members.get(a);

    this.doPortDisconnection(peerPortCM.getPortURI());
    peerPortCM.unpublishPort();
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
  public Pair<String, String> getNodeIdentifier() throws Exception {
    return new Pair<String,String>(NMSetterPort.getPortURI(), CMSetterPort.getPortURI());
  }

  @Override
  public String getNodeManagementURI() {
    return uriPrefix;
  }
  public ContentDescriptorI find(ContentTemplateI request, int hops) throws Exception {
		for (ContentDescriptorI localCd : this.contentsDescriptors) {
			if (localCd.equals(request))
				return localCd;
		}

		if (hops-- == 0)
			return null;

		for (PeerNodeAddressI node : this.members.keySet()) {
			OutboundPortCM outBoundPort = members.get(node);
			ContentDescriptorI res = ((ContentManagementCI) outBoundPort).find(request, hops);
			if (res != null)
				return res;
		}

		return null;
	}

	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops) throws Exception {
		for (ContentDescriptorI localCd : this.contentsDescriptors) {
			if (localCd.match(cd)) {
				matched.add(localCd);
			}
		}

		if (hops != 0) {
			for (PeerNodeAddressI node : this.members.keySet()) {
				OutboundPortCM outBoundPort = members.get(node);
				matched.addAll(((ContentManagementCI) outBoundPort).match(cd, matched, --hops));
			}
		}

		return matched;
	}
}
