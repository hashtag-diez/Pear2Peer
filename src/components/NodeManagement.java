package components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkScanner.NetworkScannerPlugin;
import interfaces.ContentNodeAddressI;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import ports.NodeManagementInboundPort;
import utiles.Displayer;

@OfferedInterfaces(offered = { NodeManagementCI.class, ContentManagementPI.class })
@RequiredInterfaces(required = { ContentManagementPI.class })
public class NodeManagement extends AbstractComponent implements FacadeNodeAddressI, ContentNodeAddressI {

	private static final boolean DEBUG_MODE = true;

	protected NodeManagementInboundPort NMSetterPort;

	protected Set<PeerNodeAddressI> members = new HashSet<>();
	protected ContentManagementPlugin ContentManagementPlug;
	protected NetworkScannerPlugin NetworkScannerPlug;

	protected NodeManagement(String reflectionInboundPortURI, String inboundURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 4, 0);
		this.NMSetterPort = new NodeManagementInboundPort(inboundURI, this);
		this.NMSetterPort.publishPort();

		ContentManagementPlug = new ContentManagementPlugin(DescriptorId, this);
		this.installPlugin(ContentManagementPlug);

		NetworkScannerPlug = new NetworkScannerPlugin("ns" + reflectionInboundPortURI, ContentManagementPlug);
		this.installPlugin(NetworkScannerPlug);
	}

	public synchronized Set<PeerNodeAddressI> addNewComers(PeerNodeAddressI a) throws Exception {
		Displayer.display(a.getNodeURI() + " veut se connecter au reseau.", DEBUG_MODE);
		List<PeerNodeAddressI> neighbors = new ArrayList<>(members);
		if (members.size() % 4 == 0) {
			Displayer.display("Nouvelle racine !", DEBUG_MODE);
			ContentManagementPlug.put(a);
			NetworkScannerPlug.put(a);
		}
		members.add(a);

		Set<PeerNodeAddressI> res = neighbors.stream().skip(neighbors.size() > 0 ? neighbors.size() - 1 : 0).limit(1)
				.collect(Collectors.toSet());
		return res;
	}

	/**
	 * It removes a peer from the network
	 * 
	 * @param a the peer to be deleted
	 */
	public void deletePeer(PeerNodeAddressI a) throws Exception {
		Displayer.display(a.getNodeURI() + " veut se deconnecter du reseau.", DEBUG_MODE);
		ContentManagementPlug.remove(a);
		NetworkScannerPlug.remove(a);
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
		return reflectionInboundPortURI;
	}

	@Override
	public String getContentManagementURI() {
		return "cm-" + reflectionInboundPortURI;
	}

	@Override
	public String getNodeURI() throws Exception {
		throw new UnsupportedOperationException("Unimplemented method 'getNodeURI'");
	}

}
