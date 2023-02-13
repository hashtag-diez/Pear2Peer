package components;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import components.interfaces.ContentManagementCI;
import components.interfaces.NodeCI;
import components.interfaces.NodeManagementCI;
import connectors.NodeServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.PeerNodeAddressI;
import ports.NodeInboundPort;
import ports.NodeOutboundPortN;
import ports.NodeOutboundPortNM;

@RequiredInterfaces(required = { NodeManagementCI.class, NodeCI.class })
@OfferedInterfaces(offered = { NodeCI.class, ContentManagementCI.class })
public class Node
		extends AbstractComponent
		implements PeerNodeAddressI {

	protected NodeOutboundPortNM NMGetterPort;
	protected NodeInboundPort NSetterPort;

	protected Map<PeerNodeAddressI, NodeOutboundPortN> peersGetterPorts;

	protected String uriPrefix = "NodeC";

	protected Node(String reflectionInboundPortURI, String outboundURI) throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.NMGetterPort = new NodeOutboundPortNM(outboundURI, this);
		this.NMGetterPort.publishPort();
		this.peersGetterPorts = new HashMap<PeerNodeAddressI, NodeOutboundPortN>();
		this.uriPrefix = this.uriPrefix + UUID.randomUUID();
		this.NSetterPort = new NodeInboundPort(reflectionInboundPortURI, this);
		this.NSetterPort.publishPort();
	}

	@Override
	public void execute() throws Exception {
		System.out.println("Execute de " + getNodeURI());

		Set<PeerNodeAddressI> neighbors = NMGetterPort.join(this);
		for (PeerNodeAddressI node : neighbors) {
			String oport = AbstractOutboundPort.generatePortURI();
			NodeOutboundPortN peerOutPort = new NodeOutboundPortN(oport, this);
			peerOutPort.publishPort();
			String iport = node.getNodeIdentifier();
			this.doPortConnection(oport, iport, NodeServiceConnector.class.getCanonicalName());
			this.peersGetterPorts.put(node, peerOutPort);

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
								NodeOutboundPortN out = peersGetterPorts.get(p);
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
		String oport = AbstractOutboundPort.generatePortURI();
		String iport = node.getNodeIdentifier();
		System.out.println("Ajout de " + iport);
		NodeOutboundPortN peerOutPort = new NodeOutboundPortN(oport, this);
		peerOutPort.publishPort();
		this.doPortConnection(oport, iport, NodeServiceConnector.class.getCanonicalName());
		this.peersGetterPorts.put(node, peerOutPort);
		System.out.println(getNodeIdentifier() + " est connecté à " + node.getNodeIdentifier());
		return node;
	}

	public void deleteFromNetwork(PeerNodeAddressI node) throws Exception {
		NodeOutboundPortN outBoundPort = this.peersGetterPorts.get(node);
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
	public String getNodeIdentifier() throws Exception {
		return NSetterPort.getPortURI();
	}

	@Override
	public String getNodeURI() {
		return this.uriPrefix;
	}
}
