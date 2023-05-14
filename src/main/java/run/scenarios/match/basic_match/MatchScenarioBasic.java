package main.java.run.scenarios.match.basic_match;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import main.java.components.Node;
import main.java.components.NodeManagement;
import main.java.run.scenarios.match.ClientLookingForContentWhichMatch;
import main.java.utiles.Helpers;

public class MatchScenarioBasic extends AbstractCVM {

	public MatchScenarioBasic() throws Exception {
		super();
	}

	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my_NODE";
	protected static final long DELAY_TO_START_IN_NANOS = TimeUnit.SECONDS.toNanos(5);

	protected final int NB_PEER = 50;
	protected final int NB_FACADE = 5;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;

	@Override
	public void deploy() throws Exception {
		// decide for a start time as an Instant that will be used as the base
		// time to plan all the actions of the test scenario
		Instant startInstant = Instant.now().plusSeconds(DELAY_TO_START_IN_NANOS);
		double accelerationFactor = 10.0;

		ContentDataManager.DATA_DIR_NAME = "src/data";

		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[] { Helpers.GLOBAL_CLOCK_URI, startInstant.toEpochMilli(),
						startInstant, accelerationFactor });

		for (int i = 1; i <= NB_FACADE; i++) {
			AbstractComponent.createComponent(NodeManagement.class.getCanonicalName(),
					new Object[] { NODE_MANAGEMENT_COMPONENT_URI + "-" + i, (i - 1) * 10 });
		}

		for (int i = 0; i < NB_PEER; i++) {
			AbstractComponent.createComponent(Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + "_" + i, NODE_MANAGEMENT_COMPONENT_URI + "-" + ((i % NB_FACADE) + 1),
							i });
		}

		AbstractComponent.createComponent(
				ClientLookingForContentWhichMatch.class.getCanonicalName(),
				new Object[] { "Utilisateur",
						NODE_MANAGEMENT_COMPONENT_URI + "-" + Helpers.getRandomNumber(1, 5) });

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			MatchScenarioBasic a = new MatchScenarioBasic();
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
