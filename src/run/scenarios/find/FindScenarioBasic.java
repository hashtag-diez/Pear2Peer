package run.scenarios.find;

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
	public static final String CLIENT_COMPONENT_URI = "Clios";

	protected static final long DELAY_TO_START_IN_NANOS = TimeUnit.SECONDS.toNanos(5);
	public static final String CLOCK_URI = "my-clock-uri";

	protected final int NB_PEER = 9;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;
	
	// chronologies (odres)
	public static int MOMENT_FOR_FACADE_TO_JOIN = 1;
	public static int MOMENT_FOR_PEER_TO_JOIN = 5;
	public static int MOMENT_FOR_CLIENT_TO_JOIN = 8;
	public static int MOMENT_FOR_CLIENT_TO_SEARCH = 20;
	public static int MOMENT_FOR_PEER_TO_LEAVE=1000;
	public static int MOMENT_FOR_FACADE_TO_LEAVE = 1500;
	public static int X = -1, Y=0;

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
		Instant	startInstant = Instant.parse("2023-04-17T15:37:00Z");
		double accelerationFactor = 100.0;
		
		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{CLOCK_URI, unixEpochStartTimeInNanos,
							 startInstant, accelerationFactor});
		
		String nodeManagementURI = 
		AbstractComponent.createComponent(
				NodeManagement.class.getCanonicalName(),
				new Object[] { NODE_MANAGEMENT_COMPONENT_URI + "-" + 1, 0, getX(), getY()});
		this.toggleTracing(nodeManagementURI);
		
		
		for (int i = 1; i <= NB_PEER; i++) {
			String nodeComponentUri = AbstractComponent.createComponent(
					Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + i,
						NODE_MANAGEMENT_COMPONENT_URI+ "-" + 1, i , getX(), getY()});
			
			assert	this.isDeployedComponent(nodeComponentUri);
			this.toggleTracing(nodeComponentUri);
		}
		
		AbstractComponent.createComponent(
				ClientLookingForContent.class.getCanonicalName(), 
				new Object[] {CLIENT_COMPONENT_URI,
				NODE_MANAGEMENT_COMPONENT_URI+ "-" + 1, getX(), getY()}
		);
		this.toggleTracing(CLIENT_COMPONENT_URI);
		
	}
	
	private int getX() {
		if (X == 3) {
			X = 0;
			Y++;
		} else {
			return ++X;
		}
		return X;
	}
	
	private int getY() {
		return Y;
	}
	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			FindScenarioBasic a = new FindScenarioBasic();
			// Execute the application.
			a.startStandardLifeCycle(20000L);
			// Give some time to see the traces (convenience).
			Thread.sleep(100000L);
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
