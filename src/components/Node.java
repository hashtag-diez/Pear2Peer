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
import connectors.NodeServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.utils.Pair;
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

	protected Map<PeerNodeAddressI, Pair<NodeOutboundPortN, OutboundPortCM>> peersGetterPorts;

	protected String uriPrefix = "NodeC";

	protected List<ContentDescriptorI> contentsDescriptors;

	protected Node(String reflectionInboundPortURI, String outboundURI) throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.NMGetterPort = new NodeOutboundPortNM(outboundURI, this);
		this.NMGetterPort.publishPort();
		this.peersGetterPorts = new HashMap<PeerNodeAddressI, Pair<NodeOutboundPortN, OutboundPortCM>>();
		this.uriPrefix = this.uriPrefix + UUID.randomUUID();
		this.NSetterPort = new NodeInboundPort(reflectionInboundPortURI, this);
		this.NSetterPort.publishPort();
		this.CMSetterPort = new NodeInboundPortCM("cm" + reflectionInboundPortURI, this);
		this.CMSetterPort.publishPort();
		this.contentsDescriptors = new ArrayList<ContentDescriptorI>();
	}

	@Override
	public void execute() throws Exception {
		System.out.println("Execute de " + getNodeURI());

		Set<PeerNodeAddressI> neighbors = NMGetterPort.join(this);
		for (PeerNodeAddressI node : neighbors) {
			String oportN = AbstractOutboundPort.generatePortURI();
			String oportCM = AbstractOutboundPort.generatePortURI();

			NodeOutboundPortN peerOutPort = new NodeOutboundPortN(oportN, this);
			peerOutPort.publishPort();
			String iportN = node.getNodeIdentifier().getFirst();
			this.doPortConnection(oportN, iportN, NodeServiceConnector.class.getCanonicalName());

			OutboundPortCM peerOutPortCM = new OutboundPortCM(oportCM, this);
			peerOutPort.publishPort();
			String iportNM = node.getNodeIdentifier().getSecond();
			this.doPortConnection(oportCM, iportNM, ContentManagementServiceConnector.class.getCanonicalName());

			this.peersGetterPorts.put(node, new Pair<NodeOutboundPortN, OutboundPortCM>(peerOutPort, peerOutPortCM));

			PeerNodeAddressI myID = peerOutPort.connect(this);
			System.out.println(myID.getNodeIdentifier() + " est connecté à " + node.getNodeIdentifier());
		}

		Thread.sleep((long) (Math.random() % 4) * 1000L);

		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						Node caller = (Node) this.taskOwner;

						try {
							caller.NMGetterPort.leave(caller);
							/*
							 * doPortDisconnection(NMGetterPort.getPortURI());
							 * NMGetterPort.unpublishPort();
							 */

							for (PeerNodeAddressI p : caller.peersGetterPorts.keySet()) {
								NodeOutboundPortN out = peersGetterPorts.get(p).getFirst();
								out.disconnect(caller);
								caller.doPortDisconnection(out.getPortURI());
								out.unpublishPort();
							}
							caller.peersGetterPorts.clear();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
	}

	public PeerNodeAddressI addToNetwork(PeerNodeAddressI node) throws Exception {
		String oportNM = AbstractOutboundPort.generatePortURI();
		String oportCM = AbstractOutboundPort.generatePortURI();

		String iportN = node.getNodeIdentifier().getFirst();
		System.out.println("Ajout de " + iportN);
		NodeOutboundPortN peerOutPortN = new NodeOutboundPortN(oportNM, this);
		peerOutPortN.publishPort();
		this.doPortConnection(oportNM, iportN, NodeServiceConnector.class.getCanonicalName());

		String iportCM = node.getNodeIdentifier().getSecond();
		OutboundPortCM peerOutPortCM = new OutboundPortCM(oportCM, this);
		peerOutPortCM.publishPort();
		this.doPortConnection(oportCM, iportCM, NodeServiceConnector.class.getCanonicalName());

		this.peersGetterPorts.put(node, new Pair<NodeOutboundPortN, OutboundPortCM>(peerOutPortN, peerOutPortCM));
		System.out.println(getNodeIdentifier() + " est connecté à " + node.getNodeIdentifier());
		return node;
	}

	public void deleteFromNetwork(PeerNodeAddressI node) throws Exception {
		NodeOutboundPortN outBoundPort = this.peersGetterPorts.get(node).getFirst();
		this.doPortDisconnection(outBoundPort.getPortURI());
		outBoundPort.unpublishPort();
		this.peersGetterPorts.remove(node);
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

	/**
	 * to review
	 */
	public ContentDescriptorI find(ContentTemplateI request, int hops) throws Exception {
		for (ContentDescriptorI localCd : this.contentsDescriptors) {
			if (localCd.equals(request))
				return localCd;

		}

		if (hops-- == 0)
			return null;

		for (PeerNodeAddressI node : this.peersGetterPorts.keySet()) {
			OutboundPortCM outBoundPort = peersGetterPorts.get(node).getSecond();
			ContentDescriptorI res = ((ContentManagementCI) outBoundPort).find(request, hops);
			if (res != null)
				return res;
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
				matched.addAll(((ContentManagementCI) outBoundPort).match(cd, matched, --hops));
			}
		}

		return matched;
	}
}