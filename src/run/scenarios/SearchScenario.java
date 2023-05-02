package run.scenarios;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import components.Node;
import components.NodeManagement;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

/**
 * Dans ce scenario, chacun des noeuds:
 * - se connecte au reseau,
 * - ensuite fait une tache,
 * - enfin se deconnecte.
 * 
 * La deconnexion ne commence que si tous les noeuds ont fini leur tâche.
 * On programmera la deconnexion à un instant suffisament reculé pour
 * permettre à chacun de determiner sa tâche.
 * 
 * @author aboub_bmdb7gr
 *
 */
public abstract class SearchScenario extends AbstractCVM {

	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my-NODE";
	public static final String CLIENT_COMPONENT_URI = "Clios";
	/** URI of the provider outbound port (simplifies the connection). */
	protected static final String URIGetterOutboundPortURI = "oport";
	/** URI of the consumer inbound port (simplifies the connection). */
	protected static final String URIProviderInboundPortURI = "iport";
	// Reference to the provider component to share between deploy and shutdown.
	protected String uriNodeManagement;

	protected static final long DELAY_TO_START_IN_NANOS = TimeUnit.SECONDS.toNanos(5);
	public static final String CLOCK_URI = "my-clock-uri";

	protected final int NB_PEER = 9;
	protected final int NB_FACADE = 5;

	public static int MOMENT_FOR_FACADE_TO_JOIN = 1;
	public static int MOMENT_FOR_PEER_TO_JOIN = 5;
	public static int MOMENT_FOR_CLIENT_TO_JOIN = 8;
	public static int MOMENT_FOR_CLIENT_TO_SEARCH = 20;
	public static int MOMENT_FOR_PEER_TO_LEAVE = 1000;
	public static int MOMENT_FOR_FACADE_TO_LEAVE = 1500;
	public static int X = -1, Y = 0;

	public SearchScenario() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
				+ DELAY_TO_START_IN_NANOS;
		// decide for a start time as an Instant that will be used as the base
		// time to plan all the actions of the test scenario
		Instant startInstant = Instant.parse("2023-04-17T15:37:00Z");
		double accelerationFactor = 100.0;

		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[] { CLOCK_URI, unixEpochStartTimeInNanos,
						startInstant, accelerationFactor });

		String nodeManagementURI = AbstractComponent.createComponent(
				NodeManagement.class.getCanonicalName(),
				new Object[] { NODE_MANAGEMENT_COMPONENT_URI + "-" + 1, 0, getX(), getY() });
		this.toggleTracing(nodeManagementURI);

		for (int i = 1; i <= NB_PEER; i++) {
			String nodeComponentUri = AbstractComponent.createComponent(
					Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + i,
							NODE_MANAGEMENT_COMPONENT_URI + "-" + 1, i, getX(), getY() });

			assert this.isDeployedComponent(nodeComponentUri);
			this.toggleTracing(nodeComponentUri);
		}

		this.createClientComponent();

	}

	/**
	 * Personnalisation du client en fonction
	 * de son comportement vis à vis du reseau.
	 */
	protected abstract void createClientComponent() throws Exception;

	protected int getX() {
		if (X == 3) {
			X = 0;
			Y++;
		} else {
			return ++X;
		}
		return X;
	}

	protected int getY() {
		return Y;
	}

}
