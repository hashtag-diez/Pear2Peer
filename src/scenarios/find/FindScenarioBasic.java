package scenarios.find;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import components.Node;
import components.NodeManagement;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

public class FindScenarioBasic extends AbstractCVM {
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

	protected final int NB_PEER = 9;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;

	public FindScenarioBasic() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		long unixEpochStartTimeInNanos =
				TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
				+ DELAY_TO_START_IN_NANOS;
		// decide for a start time as an Instant that will be used as the base
		// time to plan all the actions of the test scenario
		Instant	startInstant = Instant.parse("2023-03-06T15:37:00Z");
		double accelerationFactor = 10.0;
		
		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{CLOCK_URI, unixEpochStartTimeInNanos,
							 startInstant, accelerationFactor});
		
		AbstractComponent.createComponent(
				NodeManagement.class.getCanonicalName(),
				new Object[] { NODE_MANAGEMENT_COMPONENT_URI,
						URIProviderInboundPortURI, 0 });

		for (int i = 1; i <= NB_PEER; i++) {
			AbstractComponent.createComponent(
					Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + i,
							URIProviderInboundPortURI, i });
		}
		
		AbstractComponent.createComponent(
				ClientLookingForContent.class.getCanonicalName(), 
				new Object[] {"Clicos",
						NODE_MANAGEMENT_COMPONENT_URI}
		);
		
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			FindScenarioBasic a = new FindScenarioBasic();
			// Execute the application.
			a.startStandardLifeCycle(20000L);
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
