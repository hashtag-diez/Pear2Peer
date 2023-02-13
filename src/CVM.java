import components.Node;
import components.NodeManagement;
import connectors.NodeManagementServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM
		extends AbstractCVM {

	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my-NODE";
	/** URI of the provider outbound port (simplifies the connection). */
	protected static final String URIGetterOutboundPortURI = "oport";
	/** URI of the consumer inbound port (simplifies the connection). */
	protected static final String URIProviderInboundPortURI = "iport";

	/**
	 * Reference to the provider component to share between deploy
	 * and shutdown.
	 */
	protected String uriNodeManagement;
	/**
	 * Reference to the consumer component to share between deploy
	 * and shutdown.
	 */
	protected String uriNode;
	protected String uriNode1;

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		this.uriNodeManagement = AbstractComponent.createComponent(
				NodeManagement.class.getCanonicalName(),
				new Object[] { NODE_MANAGEMENT_COMPONENT_URI,
						URIProviderInboundPortURI });

		this.uriNode = AbstractComponent.createComponent(
				Node.class.getCanonicalName(),
				new Object[] { NODE_COMPONENT_URI,
						URIGetterOutboundPortURI });

		this.uriNode1 = AbstractComponent.createComponent(
				Node.class.getCanonicalName(),
				new Object[] { NODE_COMPONENT_URI + "1",
						URIGetterOutboundPortURI + "1" });

		this.doPortConnection(
				this.uriNode,
				URIGetterOutboundPortURI,
				URIProviderInboundPortURI,
				NodeManagementServiceConnector.class.getCanonicalName());

		this.doPortConnection(
				this.uriNode1,
				URIGetterOutboundPortURI + "1",
				URIProviderInboundPortURI,
				NodeManagementServiceConnector.class.getCanonicalName());

		super.deploy();
	}

	@Override
	public void finalise() throws Exception {
		this.doPortDisconnection(this.uriNode,
				URIGetterOutboundPortURI);
		super.finalise();
	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			CVM a = new CVM();
			// Execute the application.
			a.startStandardLifeCycle(2000L);
			// Give some time to see the traces (convenience).
			Thread.sleep(500L);
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
