
import components.Node;
import components.NodeManagement;
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

	protected final int NB_PEER = 4;
	/**
	 * Reference to the provider component to share between deploy
	 * and shutdown.
	 */
	protected String uriNodeManagement;

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		this.uriNodeManagement = AbstractComponent.createComponent(
				NodeManagement.class.getCanonicalName(),
				new Object[] { NODE_MANAGEMENT_COMPONENT_URI,
						URIProviderInboundPortURI });

		for (int i = 0; i < NB_PEER; i++) {
			AbstractComponent.createComponent(
					Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + i,
							URIGetterOutboundPortURI + i,
							URIProviderInboundPortURI });
		}

		super.deploy();
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
