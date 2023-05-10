package main.java.run.scenarios.find;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import main.java.components.Node;
import main.java.components.NodeManagement;
import main.java.utiles.Helpers;

public class FindScenarioBasic extends AbstractCVM {
	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my_NODE";

	protected static final long DELAY_TO_START_IN_NANOS = TimeUnit.SECONDS.toNanos(1);
	public static final String CLOCK_URI = "my-clock-uri";

	protected final int NB_PEER = 9;
	protected final int NB_FACADE = 5;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;

	public FindScenarioBasic() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
				+ DELAY_TO_START_IN_NANOS;
		// decide for a start time as an Instant that will be used as the base
		// time to plan all the actions of the test scenario
		Instant startInstant = Instant.parse("2023-04-17T15:37:00Z");
		double accelerationFactor = 10.0;

		ContentDataManager.DATA_DIR_NAME = "src/data";

		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[] { CLOCK_URI, unixEpochStartTimeInNanos,
						startInstant, accelerationFactor });

		for (int i = 1; i <= NB_FACADE; i++) {
			AbstractComponent.createComponent(NodeManagement.class.getCanonicalName(),
					new Object[] { NODE_MANAGEMENT_COMPONENT_URI + "-" + i, (i - 1) * 10 });
		}

		for (int i = 1; i <= NB_FACADE * NB_PEER; i++) {
			AbstractComponent.createComponent(Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + i, NODE_MANAGEMENT_COMPONENT_URI + "-" + ((i % NB_FACADE) + 1), i });
		}

		AbstractComponent.createComponent(
				ClientLookingForContent.class.getCanonicalName(),
				new Object[] { "Clicos",
						NODE_MANAGEMENT_COMPONENT_URI + "-" + Helpers.getRandomNumber(1, 5) });
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			FindScenarioBasic a = new FindScenarioBasic();
			// Execute the application.
			a.startStandardLifeCycle(15000L);
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