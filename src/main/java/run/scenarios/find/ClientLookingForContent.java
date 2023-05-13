package main.java.run.scenarios.find;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import main.java.components.Client;
import main.java.components.interfaces.ClientCI;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPI;
import main.java.utiles.DebugDisplayer;
import main.java.utiles.Helpers;

@OfferedInterfaces(offered = { ClientCI.class })
@RequiredInterfaces(required = { FacadeContentManagementPI.class, ClocksServerCI.class })
public class ClientLookingForContent extends Client {

	protected static final boolean DEBUG_MODE = true;
	protected ClocksServerOutboundPort csop;
	protected DebugDisplayer debugPrinter = new DebugDisplayer(DEBUG_MODE);

	protected ClientLookingForContent(String reflectionInboundPort, String CMNodeManagementInboundURI)
			throws Exception {
		super(reflectionInboundPort, CMNodeManagementInboundURI);
		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();
	}

	@Override
	public void execute() throws Exception {
		super.execute();

		scheduleClientTasks();
		debugPrinter.display("Client task [find] sheduled");
	}

	protected void scheduleClientTasks() throws Exception {
		// connexion à l'horloge
		this.doPortConnection(this.csop.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());

		AcceleratedClock clock = this.csop.getClock(Helpers.GLOBAL_CLOCK_URI);
		// recuperation de la date du scenario
		// Instant startInstant = clock.getStartInstant();

		// synchronisaiton: tous les noeuds doivent patienter jusqu'à la date
		// du rendez-vous: (startInstant)
		clock.waitUntilStart();
		Instant start = clock.getStartInstant();
		long delayInNanosToSearch = clock.nanoDelayUntilAcceleratedInstant(
			start.plusSeconds(10));

		this.scheduleTask(
				o -> {
					try {
						Thread.sleep(10000);
						((ClientLookingForContent) o).exampleSearchFind();
					} catch (Exception e) {
						e.printStackTrace();
					}
				},
				delayInNanosToSearch,
				TimeUnit.NANOSECONDS);
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}
}
