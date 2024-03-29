package main.java.run.distributed.find;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import main.java.components.Node;
import main.java.components.NodeManagement;
import main.java.run.scenarios.find.ClientLookingForContent;
import main.java.utiles.Helpers;

public class FindScenarioBasic extends AbstractDistributedCVM {
	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my_NODE";

	protected static final long DELAY_TO_START_IN_NANOS = TimeUnit.SECONDS.toNanos(1);
	public static final String CLOCK_URI = "my-clock-uri";

	protected final int NB_PEER = 9;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;

	public FindScenarioBasic(String[] args) throws Exception {
		super(args);
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		// decide for a start time as an Instant that will be used as the base
		// time to plan all the actions of the test scenario
		Instant startInstant = Instant.now().plusSeconds(DELAY_TO_START_IN_NANOS);
		double accelerationFactor = 10.0;
		ContentDataManager.DATA_DIR_NAME = "src/data";
		Integer FacadeIndex = Integer.parseInt(thisJVMURI.split("-")[2]);

		AbstractComponent.createComponent(NodeManagement.class.getCanonicalName(),
				new Object[] { thisJVMURI, (FacadeIndex - 1) * 10 });

		for (int i = 1; i <= NB_PEER; i++) {
			AbstractComponent.createComponent(Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + "_" + ((FacadeIndex - 1) * 10 + i), thisJVMURI, i });
		}
		if (thisJVMURI.equals("my-NODE_MANAGEMENT-1")) {
			AbstractComponent.createComponent(
					ClocksServer.class.getCanonicalName(),
					new Object[] { CLOCK_URI, startInstant.toEpochMilli(),
							startInstant, accelerationFactor });

			AbstractComponent.createComponent(
					ClientLookingForContent.class.getCanonicalName(),
					new Object[] { "Clicos",
							NODE_MANAGEMENT_COMPONENT_URI + "-" + Helpers.getRandomNumber(1, 5) });
		}
		super.instantiateAndPublish();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			FindScenarioBasic a = new FindScenarioBasic(args);
			// Execute the application.
			a.startStandardLifeCycle(20000L);
			// Give some time to see the traces (convenience).
			Thread.sleep(5000L);
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
