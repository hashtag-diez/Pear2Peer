package components;

import java.util.HashMap;
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
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.utils.Pair;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagementPlugin;
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
	protected NodeInboundPort NSetterPort;
	protected String NMInboundURI;
	protected Map<PeerNodeAddressI, NodeOutboundPortN> peersGetterPorts;
	private final String CM_PLUGIN_URI = "CM_PLUG";
	protected String uriPrefix = "NodeC";
	private NodeInboundPortCM CMSetterPort;

	protected Node(String reflectionInboundPortURI, String outboundURI, String NMInboundURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 4, 0);
		this.NMGetterPort = new NodeOutboundPortNM(outboundURI, this);
		this.NMGetterPort.publishPort();

		this.peersGetterPorts = new HashMap<PeerNodeAddressI, NodeOutboundPortN>();
		
		this.uriPrefix = this.uriPrefix + UUID.randomUUID();
		
		this.NSetterPort = new NodeInboundPort(reflectionInboundPortURI, this);
		this.NSetterPort.publishPort();
		
		PluginI plugin = new ContentManagementPlugin(DescriptorId) ;
		plugin.setPluginURI(CM_PLUGIN_URI) ;
		this.installPlugin(plugin); 
		
		this.CMSetterPort = new NodeInboundPortCM("cm" + reflectionInboundPortURI, this);
		this.CMSetterPort.publishPort(); 
		
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
			String oportCM = AbstractOutboundPort.generatePortURI();

			NodeOutboundPortN peerOutPort = new NodeOutboundPortN(oportN, this);
			peerOutPort.publishPort();
			String iportN = node.getNodeIdentifier().getFirst();
			this.doPortConnection(oportN, iportN, NodeServiceConnector.class.getCanonicalName());


			String iportCM = CMSetterPort.getPortURI();
			OutboundPortCM peerOutPortCM = new OutboundPortCM(oportCM, this);
			peerOutPortCM.publishPort();
			this.doPortConnection(oportCM, iportCM, ContentManagementServiceConnector.class.getCanonicalName());

			((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).put(node, peerOutPortCM);
			this.peersGetterPorts.put(node, peerOutPort);

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

		String iportCM =  node.getNodeIdentifier().getSecond();
		OutboundPortCM peerOutPortCM = new OutboundPortCM(oportCM, this);
		peerOutPortCM.publishPort();
		this.doPortConnection(oportCM, iportCM, ContentManagementServiceConnector.class.getCanonicalName());

		((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).put(node, peerOutPortCM);
		this.peersGetterPorts.put(node, peerOutPortN);
		return node;
	}

	public void deleteFromNetwork(PeerNodeAddressI node) throws Exception {
		NodeOutboundPortN outBoundPort = this.peersGetterPorts.get(node);
		OutboundPortCM outBoundPortCM = ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).get(node);

		this.doPortDisconnection(outBoundPort.getPortURI());
		this.doPortDisconnection(outBoundPortCM.getPortURI());

		outBoundPort.unpublishPort();
		outBoundPortCM.unpublishPort();

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
		return new Pair<String,String>(NSetterPort.getPortURI(), CMSetterPort.getPortURI());
	}

	@Override
	public String getNodeURI() {
		return this.uriPrefix;
	}

	public ContentDescriptorI find(ContentTemplateI request, int hops) throws Exception {
		return ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).find(request, hops);
	}


	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
			throws Exception {
		Set<ContentDescriptorI> res = ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).match(cd, matched, hops);
		return res;
	}
}