package components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

@RequiredInterfaces(required = {NodeManagementCI.class, NodeCI.class})
@OfferedInterfaces(offered = {NodeCI.class})
public class Node 
extends AbstractComponent
implements PeerNodeAddressI{

  protected NodeOutboundPortNM NMGetterPort;
	protected ArrayList<NodeOutboundPortN> NGetterPorts; 
	protected NodeInboundPort NSetterPort;
	protected Set<PeerNodeAddressI> peers = new HashSet<>();
	protected String uriPrefix = "NodeC";

  protected Node(String reflectionInboundPortURI, String outboundURI) throws Exception {
    super(reflectionInboundPortURI, 1, 0);
    this.NMGetterPort = new NodeOutboundPortNM(outboundURI, this);
		this.NMGetterPort.publishPort();
		this.NGetterPorts = new ArrayList<>();
		this.uriPrefix = this.uriPrefix + UUID.randomUUID();
		this.NSetterPort = new NodeInboundPort(reflectionInboundPortURI, this);
		this.NSetterPort.publishPort();
	}

  @Override
  public void	execute() throws Exception{
		System.out.println("Execute de " + getNodeURI());
		
    Set<PeerNodeAddressI> neighbors = NMGetterPort.join(this);
		System.out.println(neighbors.size());

		peers.addAll(neighbors);

		for(PeerNodeAddressI node : neighbors){
			String oport = AbstractOutboundPort.generatePortURI();
			NodeOutboundPortN peerOutPort = new NodeOutboundPortN(oport, this);
			peerOutPort.publishPort();
			String iport = node.getNodeIdentifier();
			this.doPortConnection(oport, iport, NodeServiceConnector.class.getCanonicalName());
			NGetterPorts.add(peerOutPort);

			PeerNodeAddressI myID = NGetterPorts.get(NGetterPorts.size()-1).connect(this);
			System.out.println(myID.getNodeIdentifier() + " est connecté à " + node.getNodeIdentifier());
		}
  }
	public PeerNodeAddressI addToNetwork(PeerNodeAddressI node) throws Exception{
		String oport = AbstractOutboundPort.generatePortURI();
		String iport = node.getNodeIdentifier();
		System.out.println("Ajout de " + iport);
		NodeOutboundPortN peerOutPort = new NodeOutboundPortN(oport, this);
		peerOutPort.publishPort();
		this.doPortConnection(oport, iport, NodeServiceConnector.class.getCanonicalName());
		NGetterPorts.add(peerOutPort);		

		peers.add(node);

		System.out.println(getNodeIdentifier() + " est connecté à " + node.getNodeIdentifier());
		
		return node;
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
