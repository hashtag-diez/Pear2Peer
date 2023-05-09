package main.java.run.distributed.connect_disconnect;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.helpers.TracerWindow;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import main.java.components.Node;
import main.java.components.NodeManagement;
import main.java.utiles.Helpers;

/**
 * 
 * TUTO :
 * - Au préalable, avoir lancé start_cyclebarrier.sh et start_gregistry.sh
 * - Lancer la JVM avec Run sur Visual Studio Code
 * - Aller sur le main
 * - Taper Run
 * - Cancel la runtime lancée
 * - Supprimer tout ce qu'il y'a après le .jar généré par VSCode
 * - Mettre à la suite :
 * -Djava.security.manager
 * -Djava.security.policy=dcvm.policy
 * main.java.run.distributed.connect_disconnect.ConnectionDisconnectionScenario
 * my-NODE_MANAGEMENT-1
 * config.xml
 * - Relancer
 * 
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
public class ConnectionDisconnectionScenario extends AbstractDistributedCVM {

	public ConnectionDisconnectionScenario(String[] args) throws Exception {
		super(args);
	}

	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my-NODE";
	/** URI of the provider outbound port (simplifies the connection). */
	protected static final String URIGetterOutboundPortURI = "oport";
	/** URI of the consumer inbound port (simplifies the connection). */
	protected static final String URIProviderInboundPortURI = "iport";

	protected static final long DELAY_TO_START_IN_NANOS = TimeUnit.SECONDS.toNanos(1);

	protected final int NB_PEER = 9;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;

	@Override
	public void instantiateAndPublish() throws Exception {
		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
				+ DELAY_TO_START_IN_NANOS;
		// decide for a start time as an Instant that will be used as the base
		// time to plan all the actions of the test scenario
		Instant startInstant = Instant.parse("2023-03-06T15:37:00Z");
		double accelerationFactor = 1.0;
		ContentDataManager.DATA_DIR_NAME = "src/data";
		TracerWindow tracer = new TracerWindow(this.thisJVMURI, 0, 1);
		tracer.toggleTracing();
		tracer.traceMessage("HIII");
		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[] { Helpers.GLOBAL_CLOCK_URI, unixEpochStartTimeInNanos,
						startInstant, accelerationFactor });

		int FacadeIndex = Integer.parseInt(thisJVMURI.split("-")[2]);

		AbstractComponent.createComponent(NodeManagement.class.getCanonicalName(),
				new Object[] { thisJVMURI, (FacadeIndex - 1) * 10 });

		for (int i = 1; i <= NB_PEER; i++) {
			AbstractComponent.createComponent(Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + (FacadeIndex * i), thisJVMURI, i });
		}
		super.instantiateAndPublish();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			ConnectionDisconnectionScenario a = new ConnectionDisconnectionScenario(args);
			a.startStandardLifeCycle(20000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
