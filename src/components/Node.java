package components;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import components.interfaces.NodeCI;
import components.interfaces.NodeManagementCI;
import connectors.NodeManagementServiceConnector;
import connectors.NodeServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkScanner.NetworkScannerPlugin;
import interfaces.ContentNodeAddressI;
import interfaces.PeerNodeAddressI;
import ports.NodeInboundPort;
import ports.NodeOutboundPortN;
import ports.NodeOutboundPortNM;
import utiles.Displayer;

@RequiredInterfaces(required = { NodeManagementCI.class, NodeCI.class })
@OfferedInterfaces(offered = { NodeCI.class })
public class Node extends AbstractComponent implements ContentNodeAddressI {

	private static final boolean DEBUG_MODE = false;

	// The port used to connect to the NodeManagement component.
	protected NodeOutboundPortNM NMGetterPort;

	// The port used to be called by other nodes component.
	protected NodeInboundPort NSetterPort;
	protected String NMInboundURI;

	// A map of all the peers that this node is connected to.
	protected Map<PeerNodeAddressI, NodeOutboundPortN> peersGetterPorts;
	protected String uriPrefix = "NodeC";

	// Creating the plugins that will be used by the node.
	protected ContentManagementPlugin ContentManagementPlug;
	protected NetworkScannerPlugin NetworkScannerPlug;

	protected Node(String reflectionInboundPortURI, String NMInboundURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 5, 0);
		this.uriPrefix += UUID.randomUUID();

		this.NMGetterPort = new NodeOutboundPortNM(this);
		this.NMGetterPort.publishPort();
		this.NSetterPort = new NodeInboundPort(reflectionInboundPortURI, this);
		this.NSetterPort.publishPort();
		this.peersGetterPorts = new HashMap<PeerNodeAddressI, NodeOutboundPortN>();

		ContentManagementPlug = new ContentManagementPlugin(DescriptorId, this);
		this.installPlugin(ContentManagementPlug);

		NetworkScannerPlug = new NetworkScannerPlugin("plug" + reflectionInboundPortURI, ContentManagementPlug);
		this.installPlugin(NetworkScannerPlug);

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
		joinNetwork();
		doSomething();
		leaveNetwork();
	}

	private void doSomething() throws Exception {
		System.out.println(this.getNodeURI() + " started task");
		Thread.sleep(2000);
		System.out.println(this.getNodeURI() + " finished task");
	}

	private void joinNetwork() throws Exception {
		Set<PeerNodeAddressI> neighbors = NMGetterPort.join(this);
		for (PeerNodeAddressI node : neighbors) {
			addToNetwork(node);
			this.peersGetterPorts.get(node).connect(this);
		}
	}

	private void leaveNetwork() throws Exception {
		NMGetterPort.leave(this);
	}

	/**
	 * It connects to the peer node, adds it to the content management and network
	 * scanner plugs, and stores the outbound port in the peersGetterPorts map
	 * 
	 * @param node the node to add to the network
	 * @return The node that was added to the network.
	 */
	public PeerNodeAddressI addToNetwork(PeerNodeAddressI node) throws Exception {
		String iportN = node.getNodeIdentifier();
		NodeOutboundPortN peerOutPortN = new NodeOutboundPortN(this);
		peerOutPortN.publishPort();
		this.doPortConnection(peerOutPortN.getPortURI(), iportN, NodeServiceConnector.class.getCanonicalName());

		ContentManagementPlug.put(node);
		NetworkScannerPlug.put(node);
		this.peersGetterPorts.put(node, peerOutPortN);
		return node;
	}

	/**
	 * It deletes a peer from the network
	 * 
	 * @param node the node to be deleted from the network
	 */
	public void deleteFromNetwork(PeerNodeAddressI node) throws Exception {
		NodeOutboundPortN outBoundPort = this.peersGetterPorts.get(node);
		this.doPortDisconnection(outBoundPort.getPortURI());
		outBoundPort.unpublishPort();
		this.peersGetterPorts.remove(node);
		ContentManagementPlug.remove(node);
		NetworkScannerPlug.remove(node);
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
		return reflectionInboundPortURI;
	}

	@Override
	public String getContentManagementURI() {
		return "cm-" + reflectionInboundPortURI;
	}
}