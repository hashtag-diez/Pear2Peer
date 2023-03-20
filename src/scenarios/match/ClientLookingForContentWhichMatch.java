package scenarios.match;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import components.Client;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import scenarios.connect_disconnect.ConnectionDisconnectionScenario;
import utiles.Displayer;

public class ClientLookingForContentWhichMatch extends Client {

	private static final boolean DEBUG_MODE = true;
	protected ClocksServerOutboundPort csop;

	protected ClientLookingForContentWhichMatch(String reflectionInboundPort, String CMNodeManagementInboundURI)
			throws Exception {
		super(reflectionInboundPort, CMNodeManagementInboundURI);

		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();
	}

	@Override
	public void execute() throws Exception {
		super.execute();

		scheduleClientTasks();

		Displayer.display("Client task [match] sheduled", DEBUG_MODE);
	}

	private void scheduleClientTasks() throws Exception {
		// connexion à l'horloge
		this.doPortConnection(this.csop.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());

		AcceleratedClock clock = this.csop.getClock(ConnectionDisconnectionScenario.CLOCK_URI);
		// recuperation de la date du scenario
		Instant startInstant = clock.getStartInstant();

		// synchronisaiton: tous les noeuds doivent patienter jusqu'à la date
		// du rendez-vous: (startInstant)
		clock.waitUntilStart();
	
		long delayInNanosToSearch =
				clock.nanoDelayUntilAcceleratedInstant(
												startInstant.plusSeconds(5));
		
		this.scheduleTask(
				o -> {
					try {
						((ClientLookingForContentWhichMatch) o).exampleSearchContainsWichMatch();
						mapNetwork();
					} catch (Exception e) {
						e.printStackTrace();
					}
				},
				delayInNanosToSearch,
				TimeUnit.NANOSECONDS);
	}
	
}
