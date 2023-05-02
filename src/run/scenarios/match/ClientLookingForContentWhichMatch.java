package run.scenarios.match;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import components.Client;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import run.scenarios.SearchScenario;
import utiles.DebugDisplayer;

public class ClientLookingForContentWhichMatch extends Client {

	private static final boolean DEBUG_MODE = true;
	protected ClocksServerOutboundPort csop;
	private DebugDisplayer debugPrinter = new DebugDisplayer(DEBUG_MODE);

	protected ClientLookingForContentWhichMatch(String reflectionInboundPort, String CMNodeManagementInboundURI,
			int relativeX, int relativeY)
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
		debugPrinter.display("Client task [match] sheduled");
	}

	private void scheduleClientTasks() throws Exception {
		// connexion à l'horloge
		this.doPortConnection(this.csop.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());

		AcceleratedClock clock = this.csop.getClock(SearchScenario.CLOCK_URI);
		// recuperation de la date du scenario
		Instant startInstant = clock.getStartInstant();

		// synchronisaiton: tous les noeuds doivent patienter jusqu'à la date
		// du rendez-vous: (startInstant)
		clock.waitUntilStart();

		long delayInNanosToSearch = clock.nanoDelayUntilAcceleratedInstant(
				startInstant.plusSeconds(5));

		this.scheduleTask(
				o -> {
					try {
						((ClientLookingForContentWhichMatch) o).exampleSearchContainsWichMatch();
					} catch (Exception e) {
						e.printStackTrace();
					}
				},
				delayInNanosToSearch,
				TimeUnit.NANOSECONDS);
	}

}
