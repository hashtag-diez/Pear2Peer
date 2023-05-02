package run.scenarios.find;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import components.Client;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import run.scenarios.connect_disconnect.ConnectionDisconnectionScenario;
import utiles.DebugDisplayer;

public class ClientLookingForContent extends Client {

	private static final boolean DEBUG_MODE = true;
	protected ClocksServerOutboundPort csop;
	private DebugDisplayer debugPrinter = new DebugDisplayer(DEBUG_MODE);

	protected ClientLookingForContent(String reflectionInboundPort, String CMNodeManagementInboundURI, int relativeX, int relativeY)
			throws Exception {
		super(reflectionInboundPort, CMNodeManagementInboundURI);
		this.getTracer().setRelativePosition(relativeX, relativeY);
		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();
	}

	@Override
	public void execute() throws Exception {
		super.execute();

		scheduleClientTasks();
		traceMessage("Client task [find] sheduled");
		
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

		long delayInNanosToSearch = clock.nanoDelayUntilAcceleratedInstant(
				startInstant.plusSeconds(FindScenarioBasic.MOMENT_FOR_CLIENT_TO_SEARCH));

		this.scheduleTask(
				o -> {
					try {
						((ClientLookingForContent) o).exampleSearchFind();
					} catch (Exception e) {
						e.printStackTrace();
					}
				},
				delayInNanosToSearch,
				TimeUnit.NANOSECONDS);
	}
}
