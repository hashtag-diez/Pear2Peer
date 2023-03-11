package scenarios.connect_disconnect;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import components.Node;
import components.NodeManagement;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import scenarios.find.ClientLookingForContent;
import scenarios.find.FindScenarioBasic;

public class ConnectionDisconnectionScenario extends AbstractCVM {

	public ConnectionDisconnectionScenario() throws Exception {
		super();
	}

	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my-NODE";
	/** URI of the provider outbound port (simplifies the connection). */
	protected static final String URIGetterOutboundPortURI = "oport";
	/** URI of the consumer inbound port (simplifies the connection). */
	protected static final String URIProviderInboundPortURI = "iport";

	protected static final long DELAY_TO_START_IN_NANOS = TimeUnit.SECONDS.toNanos(5);
	public static final String CLOCK_URI = "my-clock-uri";

	protected final int NB_PEER = 4;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;

	@Override
	public void deploy() throws Exception {

		AbstractComponent.createComponent(NodeManagement.class.getCanonicalName(),
				new Object[] { NODE_MANAGEMENT_COMPONENT_URI, URIProviderInboundPortURI, -1 });

		for (int i = 0; i < NB_PEER; i++) {
			AbstractComponent.createComponent(Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + i, URIProviderInboundPortURI, i });
		}

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			ConnectionDisconnectionScenario a = new ConnectionDisconnectionScenario();
			// Execute the application.
			a.startStandardLifeCycle(10000L);
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
