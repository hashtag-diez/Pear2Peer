package main.java.run.distributed.find;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import main.java.components.Node;
import main.java.components.NodeManagement;
import main.java.run.scenarios.find.total_find.ClientLookingForAllContent;
import main.java.utiles.Helpers;

public class FindBigNetwork extends AbstractDistributedCVM {
	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my_NODE";

	protected static final long DELAY_TO_START_IN_NANOS = TimeUnit.SECONDS.toNanos(2);

	protected final int NB_PEER = 10;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;

	public FindBigNetwork(String[] args) throws Exception {
		super(args);
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		// decide for a start time as an Instant that will be used as the base
		// time to plan all the actions of the test scenario
		Instant startInstant = Instant.now().plusSeconds(DELAY_TO_START_IN_NANOS);
		double accelerationFactor = 10.0;
		Integer FacadeIndex = Integer.parseInt(thisJVMURI.split("-")[2]);

		ContentDataManager.DATA_DIR_NAME = "src/data";

		AbstractComponent.createComponent(NodeManagement.class.getCanonicalName(),
						new Object[] { thisJVMURI, (FacadeIndex - 1) * 10 });
		
		for (int i = 0; i <= NB_PEER; i++) {
			AbstractComponent.createComponent(Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + "_" + ((FacadeIndex - 1) * 10 + i), thisJVMURI, i });
		}
		if (thisJVMURI.equals("my-NODE_MANAGEMENT-1")) {
			AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[] { Helpers.GLOBAL_CLOCK_URI, startInstant.toEpochMilli(),
						startInstant, accelerationFactor });

			AbstractComponent.createComponent(
				ClientLookingForAllContent.class.getCanonicalName(),
				new Object[] { "Utilisateur",
						NODE_MANAGEMENT_COMPONENT_URI + "-" + Helpers.getRandomNumber(1, 5) });
		}
		super.instantiateAndPublish();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			FindBigNetwork a = new FindBigNetwork(args);
			// Execute the application.
			a.startStandardLifeCycle(130000L);
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
