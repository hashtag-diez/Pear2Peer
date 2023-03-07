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
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.utils.Pair;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagementPlugin;
import ports.NodeInboundPort;
import ports.NodeOutboundPortN;
import ports.NodeOutboundPortNM;

@RequiredInterfaces(required = { NodeManagementCI.class, NodeCI.class })
@OfferedInterfaces(offered = { NodeCI.class })
public class Node
		extends AbstractComponent
		implements PeerNodeAddressI {

	protected NodeOutboundPortNM NMGetterPort;
	protected NodeInboundPort NSetterPort;
	protected String NMInboundURI;
	protected Map<PeerNodeAddressI, NodeOutboundPortN> peersGetterPorts;
	private final String CM_PLUGIN_URI = "CM_PLUG";
	protected String uriPrefix = "NodeC";

	protected Node(String reflectionInboundPortURI, String outboundURI, String NMInboundURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 8, 0);
		this.NMGetterPort = new NodeOutboundPortNM(outboundURI, this);
		this.NMGetterPort.publishPort();

		this.peersGetterPorts = new HashMap<PeerNodeAddressI, NodeOutboundPortN>();
		
		this.uriPrefix = this.uriPrefix + UUID.randomUUID();
		
		this.NSetterPort = new NodeInboundPort(reflectionInboundPortURI, this);
		this.NSetterPort.publishPort();

		PluginI plugin = new ContentManagementPlugin(DescriptorId);
		plugin.setPluginURI(CM_PLUGIN_URI) ;
		this.installPlugin(plugin); 
		
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

			NodeOutboundPortN peerOutPort = new NodeOutboundPortN(oportN, this);
			peerOutPort.publishPort();
			String iportN = node.getNodeIdentifier().getFirst();
			this.doPortConnection(oportN, iportN, NodeServiceConnector.class.getCanonicalName());

			((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).put(node);
			this.peersGetterPorts.put(node, peerOutPort);

			peerOutPort.connect(this);
		}
	}

	public PeerNodeAddressI addToNetwork(PeerNodeAddressI node) throws Exception {
		String oportNM = AbstractOutboundPort.generatePortURI();

		String iportN = node.getNodeIdentifier().getFirst();
		NodeOutboundPortN peerOutPortN = new NodeOutboundPortN(oportNM, this);
		peerOutPortN.publishPort();
		this.doPortConnection(oportNM, iportN, NodeServiceConnector.class.getCanonicalName());

		((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).put(node);
		this.peersGetterPorts.put(node, peerOutPortN);
		return node;
	}

	public void deleteFromNetwork(PeerNodeAddressI node) throws Exception {
		NodeOutboundPortN outBoundPort = this.peersGetterPorts.get(node);

		this.doPortDisconnection(outBoundPort.getPortURI());

		outBoundPort.unpublishPort();

		this.peersGetterPorts.remove(node);
		((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).remove(node);
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
		return new Pair<String,String>(NSetterPort.getPortURI(), ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).CMSetterPort.getPortURI());
	}

	@Override
	public String getNodeURI() {
    return reflectionInboundPortURI;
	}
}