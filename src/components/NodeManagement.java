package components;

import fr.sorbonne_u.components.AbstractComponent;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkFacade.NodeManagementPlugin;
import plugins.NetworkScanner.NetworkScannerPlugin;
import interfaces.ContentNodeAddressI;
import interfaces.FacadeNodeAddressI;

public class NodeManagement extends AbstractComponent implements FacadeNodeAddressI, ContentNodeAddressI {

	private NodeManagementPlugin plugin;

	protected NodeManagement(String reflectionInboundPortURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 8, 0);

		ContentManagementPlugin ContentManagementPlug = new ContentManagementPlugin(DescriptorId, this);
		this.installPlugin(ContentManagementPlug);

		NetworkScannerPlugin NetworkScannerPlug = new NetworkScannerPlugin(ContentManagementPlug);
		this.installPlugin(NetworkScannerPlug);

		plugin = new NodeManagementPlugin(ContentManagementPlug, NetworkScannerPlug);
		this.installPlugin(plugin);
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
		return this.plugin.getPluginURI();
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
