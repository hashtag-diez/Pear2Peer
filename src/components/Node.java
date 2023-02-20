package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import components.interfaces.ContentManagementCI;
import components.interfaces.NodeCI;
import components.interfaces.NodeManagementCI;
import connectors.ContentManagementServiceConnector;
import connectors.NodeManagementServiceConnector;
import connectors.NodeServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.Pair;
import implem.ContentDescriptor;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.PeerNodeAddressI;
import ports.NodeInboundPort;
import ports.NodeInboundPortCM;
import ports.NodeOutboundPortN;
import ports.NodeOutboundPortNM;
import ports.OutboundPortCM;

@RequiredInterfaces(required = { NodeManagementCI.class, NodeCI.class, ContentManagementCI.class })
@OfferedInterfaces(offered = { NodeCI.class, ContentManagementCI.class, ContentManagementCI.class })
public class Node
		extends AbstractComponent
		implements PeerNodeAddressI {

	protected NodeOutboundPortNM NMGetterPort;
	protected NodeInboundPortCM CMSetterPort;
	protected NodeInboundPort NSetterPort;
	protected String NMInboundURI;
	protected Map<PeerNodeAddressI, Pair<NodeOutboundPortN, OutboundPortCM>> peersGetterPorts;

	protected String uriPrefix = "NodeC";

	protected List<ContentDescriptorI> contentsDescriptors;

	protected Node(String reflectionInboundPortURI, String outboundURI, String NMInboundURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 2, 0);
		this.NMGetterPort = new NodeOutboundPortNM(outboundURI, this);
		this.NMGetterPort.publishPort();

		this.peersGetterPorts = new HashMap<PeerNodeAddressI, Pair<NodeOutboundPortN, OutboundPortCM>>();
		
		this.uriPrefix = this.uriPrefix + UUID.randomUUID();
		
		this.NSetterPort = new NodeInboundPort(reflectionInboundPortURI, this);
		this.NSetterPort.publishPort();
		
		this.CMSetterPort = new NodeInboundPortCM("cm" + reflectionInboundPortURI, this);
		this.CMSetterPort.publishPort();
		
		this.contentsDescriptors = new ArrayList<ContentDescriptorI>();
		this.loadDescriptors(6 + DescriptorId);
		this.NMInboundURI = NMInboundURI;
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		try {
			this.doPortConnection(NMGetterPort.getPortURI(), NMInboundURI,
					NodeManagementServiceConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
		this.doPortDisconnection(NMGetterPort.getPortURI());
	}

	@Override
	public void execute() throws Exception {
		Set<PeerNodeAddressI> neighbors = NMGetterPort.join(this);
		for (PeerNodeAddressI node : neighbors) {
			String oportN = AbstractOutboundPort.generatePortURI();
			// String oportCM = AbstractOutboundPort.generatePortURI();

			NodeOutboundPortN peerOutPort = new NodeOutboundPortN(oportN, this);
			peerOutPort.publishPort();
			String iportN = node.getNodeIdentifier().getFirst();
			this.doPortConnection(oportN, iportN, NodeServiceConnector.class.getCanonicalName());

			this.peersGetterPorts.put(node, new Pair<NodeOutboundPortN, OutboundPortCM>(peerOutPort, null));

			peerOutPort.connect(this);
		}
	}

	public PeerNodeAddressI addToNetwork(PeerNodeAddressI node) throws Exception {
		String oportNM = AbstractOutboundPort.generatePortURI();
		String oportCM = AbstractOutboundPort.generatePortURI();

		String iportN = node.getNodeIdentifier().getFirst();
		NodeOutboundPortN peerOutPortN = new NodeOutboundPortN(oportNM, this);
		peerOutPortN.publishPort();
		this.doPortConnection(oportNM, iportN, NodeServiceConnector.class.getCanonicalName());

		String iportCM = node.getNodeIdentifier().getSecond();
		OutboundPortCM peerOutPortCM = new OutboundPortCM(oportCM, this);
		peerOutPortCM.publishPort();
		this.doPortConnection(oportCM, iportCM, ContentManagementServiceConnector.class.getCanonicalName());

		this.peersGetterPorts.put(node, new Pair<NodeOutboundPortN, OutboundPortCM>(peerOutPortN, peerOutPortCM));
		System.out.println(getNodeIdentifier().getFirst() + " et " + node.getNodeIdentifier().getFirst() + " sont interconnectés.");
		return node;
	}

	public void deleteFromNetwork(PeerNodeAddressI node) throws Exception {
		NodeOutboundPortN outBoundPort = this.peersGetterPorts.get(node).getFirst();
		OutboundPortCM outBoundPortCM = this.peersGetterPorts.get(node).getSecond();

		this.doPortDisconnection(outBoundPort.getPortURI());
		this.doPortDisconnection(outBoundPortCM.getPortURI());

		outBoundPort.unpublishPort();
		outBoundPortCM.unpublishPort();

		this.peersGetterPorts.remove(node);
	}

	public void loadDescriptors(int number) throws Exception {
		ContentDataManager.DATA_DIR_NAME = "src/data";
		ArrayList<HashMap<String, Object>> result = ContentDataManager.readDescriptors(number);
		for (HashMap<String, Object> obj : result) {
			ContentDescriptorI readDescriptor = new ContentDescriptor(obj);
			contentsDescriptors.add(readDescriptor);
			System.out.println(readDescriptor.getComposers());
		}
	}

	@Override
	public boolean isFacade() {
		return false;
	}

	@Override
	public boolean isPeer() {
		return true;
	}

	@Override
	public Pair<String, String> getNodeIdentifier() throws Exception {
		return new Pair<String, String>(NSetterPort.getPortURI(), CMSetterPort.getPortURI());
	}

	@Override
	public String getNodeURI() {
		return this.uriPrefix;
	}

	public ContentDescriptorI find(ContentTemplateI request, int hops) throws Exception {
		for (ContentDescriptorI localCd : this.contentsDescriptors) {
			if (localCd.match(request)){
				return localCd;
			}	
		}
		System.out.println(this.getNodeIdentifier().getFirst() + " n'a pas");
		if (hops-- == 0)
			return null;

		for (PeerNodeAddressI node : this.peersGetterPorts.keySet()) {
			OutboundPortCM outBoundPort = peersGetterPorts.get(node).getSecond();
			if (outBoundPort != null) {
				ContentDescriptorI res = ((ContentManagementCI) outBoundPort).find(request, hops);
				if (res != null)
					return res;
			}
		}

		return null;
	}

	/**
	 * to review
	 */
	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
			throws Exception {
		for (ContentDescriptorI localCd : this.contentsDescriptors) {
			if (localCd.match(cd)) {
				matched.add(localCd);
			}
		}

		if (hops != 0) {
			for (PeerNodeAddressI node : this.peersGetterPorts.keySet()) {
				OutboundPortCM outBoundPort = peersGetterPorts.get(node).getSecond();
				if (outBoundPort != null) {
					matched.addAll(((ContentManagementCI) outBoundPort).match(cd, matched, --hops));
				}
			}
		}
		return matched;
	}
}