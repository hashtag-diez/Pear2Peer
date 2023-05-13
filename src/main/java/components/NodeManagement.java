package main.java.components;

import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.components.helpers.Logger;
import fr.sorbonne_u.components.helpers.TracerWindow;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import main.java.implem.ApplicationNode;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPlugin;
import main.java.plugins.NetworkFacade.NodeManagementPlugin;
import main.java.run.scenarios.connect_disconnect.ConnectionDisconnectionScenario;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

@RequiredInterfaces(required = { ClocksServerCI.class })
public class NodeManagement extends AbstractComponent {

	private NodeManagementPlugin plugin;

	protected ClocksServerOutboundPort csop;
	// private static final boolean DEBUG_MODE = true;

	private ApplicationNode app;
	private static final int DEFAULT_NB_OF_THREADS = 12;

	private static final String NM_EXECUTION_SERVICE_URI = "app-networkmanagement-tasks-execution-service";
	private static final String CM_EXECUTION_SERVICE_URI = "app-content-tasks-execution-service";

	/** Execution log of the cyclic barrier. */
	protected final Logger executionLog;
	/** Tracer of the cyclic barrier. */
	protected final TracerWindow tracer;

	protected NodeManagement(String reflectionInboundPortURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, DEFAULT_NB_OF_THREADS, DEFAULT_NB_OF_THREADS);
		this.initialise(DEFAULT_NB_OF_THREADS);

		this.tracer = new TracerWindow();
		this.executionLog = new Logger(reflectionInboundPortURI);
		tracer.toggleTracing();
		executionLog.toggleLogging();

		String NodeManagementURI = AbstractPort.generatePortURI();
		String ContentManagementURI = AbstractPort.generatePortURI();
		app = new ApplicationNode(NodeManagementURI, ContentManagementURI, reflectionInboundPortURI);
		Integer FacadeIndex = Integer.parseInt(reflectionInboundPortURI.split("-")[2]);

		FacadeContentManagementPlugin ContentManagementPlug = new FacadeContentManagementPlugin(ContentManagementURI,
				DescriptorId, app);
		ContentManagementPlug.setPreferredExecutionServiceURI(CM_EXECUTION_SERVICE_URI + "-" + FacadeIndex.toString());
		this.installPlugin(ContentManagementPlug);

		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();

		plugin = new NodeManagementPlugin(NodeManagementURI, ContentManagementPlug);
		plugin.setPreferredExecutionServiceURI(NM_EXECUTION_SERVICE_URI + "-" + FacadeIndex.toString());
		this.installPlugin(plugin);
	}

	protected void initialise(int nbThreads) {
		assert nbThreads >= 4 : "Contrainte sur le nombre de threads [" + DEFAULT_NB_OF_THREADS + "]";
		int nbThreadsNetwork = 6;
		int nbThreadsContent = nbThreads - nbThreadsNetwork;

		// this.createNewExecutorService(NS_EXECUTION_SERVICE_URI, nbThreadsNetwork,
		// false);
		Integer FacadeIndex = Integer.parseInt(reflectionInboundPortURI.split("-")[2]);
		this.createNewExecutorService(CM_EXECUTION_SERVICE_URI + "-" + FacadeIndex.toString(), nbThreadsContent, true);
		this.createNewExecutorService(NM_EXECUTION_SERVICE_URI + "-" + FacadeIndex.toString(), nbThreadsNetwork, true);
	}

	@Override
	public void execute() throws Exception {
		scheduleTasks();
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
		this.doPortDisconnection(csop.getPortURI());
		csop.unpublishPort();

		Set<String> URIs = new HashSet<>(this.portURIs2ports.keySet());
		for (String uri : URIs) {
			PortI port = this.portURIs2ports.get(uri);
			try {
				if (port.connected()) {
					this.doPortDisconnection(port.getPortURI());
				}
			} catch (ConnectionException e) {

			} finally {
				if (port.isPublished())
					port.unpublishPort();
				if (!port.isDestroyed())
					port.destroyPort();
			}
		}
	}

	private void scheduleTasks() throws Exception {
		writeMessage("[nodemanagament interconnect network] has been scheduled");
		// connexion à l'horloge
		this.doPortConnection(this.csop.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());
		writeMessage("AAAAA");
		try {
			AcceleratedClock clock = this.csop.getClock(ConnectionDisconnectionScenario.CLOCK_URI);
			writeMessage("BBBBB");
			// recuperation de la date du scenario
			Instant start = clock.getStartInstant();
			writeMessage("CCCCC");

			// synchronisaiton: tous les noeuds doivent patienter jusqu'à la date
			// du rendez-vous: (startInstant)
			clock.waitUntilStart();
			writeMessage("DDDDD");
			Random r = new Random();
      int delay = r.nextInt(2 - 1) + 1;
			long delayInNanosToJoin = clock.nanoDelayUntilAcceleratedInstant(start.plusSeconds(2 + delay));
			writeMessage("BBBBB");
			scheduleConnectionWithFacades(delayInNanosToJoin);
		} catch (Exception e) {
			// writeMessage(e.getMessage());
		}
	}

	private void scheduleConnectionWithFacades(long delayInNanosToJoin) throws AssertionError {
		this.scheduleTask(o -> {
			try {
				this.plugin.connectWithFacade();
			} catch (Exception e) {

			}
		}, delayInNanosToJoin, TimeUnit.NANOSECONDS);
	}

	public ApplicationNode getApplicationNode() {
		return app;
	}

	public void writeMessage(String msg) {
		this.executionLog.logMessage(msg);
		this.tracer.traceMessage(System.currentTimeMillis() + "|" + msg + "\n");
	}
}
