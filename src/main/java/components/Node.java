package main.java.components;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import main.java.implem.ContentNode;
import main.java.plugins.ContentManagement.ContentManagementPlugin;
import main.java.plugins.NetworkNode.NodePlugin;
import main.java.plugins.NetworkScanner.NetworkScannerPlugin;
import main.java.run.scenarios.connect_disconnect.ConnectionDisconnectionScenario;
import main.java.utiles.DebugDisplayer;
import main.java.utiles.Helpers;

@RequiredInterfaces(required = { ClocksServerCI.class })
public class Node extends AbstractComponent {

	protected ClocksServerOutboundPort csop;

	protected ContentNode node;

	private NodePlugin plugin;

	private static final int DEFAULT_NB_OF_THREADS = 6;
	private static final boolean DEBUG_MODE = true;
	protected DebugDisplayer debugPrinter = new DebugDisplayer(DEBUG_MODE);

	// private static final String NS_EXECUTION_SERVICE_URI =
	// "networkscanner-tasks-execution-service";
	private static final String NM_EXECUTION_SERVICE_URI = "networkmanagement-tasks-execution-service";
	private static final String CM_EXECUTION_SERVICE_URI = "content-tasks-execution-service";

	protected Node(String reflectionInboundPortURI, String NMInboundURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, DEFAULT_NB_OF_THREADS, DEFAULT_NB_OF_THREADS);
		this.initialise(DEFAULT_NB_OF_THREADS);

		String NodeURI = AbstractPort.generatePortURI();
		String ContentManagementURI = AbstractPort.generatePortURI();
		node = new ContentNode(NodeURI, ContentManagementURI, reflectionInboundPortURI);

		ContentManagementPlugin ContentManagementPlug = new ContentManagementPlugin(ContentManagementURI, DescriptorId,
				node);
		ContentManagementPlug.setPreferredExecutionServiceURI(CM_EXECUTION_SERVICE_URI);
		this.installPlugin(ContentManagementPlug);

		NetworkScannerPlugin NetworkScannerPlug = new NetworkScannerPlugin(ContentManagementPlug);
		// NetworkScannerPlug.setPreferredExecutionServiceURI(CM_EXECUTION_SERVICE_URI);
		this.installPlugin(NetworkScannerPlug);

		plugin = new NodePlugin(NMInboundURI, NodeURI, ContentManagementPlug, NetworkScannerPlug);
		plugin.setPreferredExecutionServiceURI(NM_EXECUTION_SERVICE_URI);
		this.installPlugin(plugin); // ! Can't reflect if not started

		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();
	}

	/**
	 * This function initializes and creates executor services for network and
	 * content threads based on the
	 * given number of threads.
	 * 
	 * @param nbThreads The total number of threads to be used for execution.
	 */
	protected void initialise(int nbThreads) {
		assert nbThreads >= 4 : "Contrainte sur le nombre de threads [" + DEFAULT_NB_OF_THREADS + "]";
		int nbThreadsNetwork = 3;
		int nbThreadsContent = nbThreads - nbThreadsNetwork;

		// this.createNewExecutorService(NS_EXECUTION_SERVICE_URI, nbThreadsNetwork,
		// false);
		this.createNewExecutorService(CM_EXECUTION_SERVICE_URI, nbThreadsContent, false);
		this.createNewExecutorService(NM_EXECUTION_SERVICE_URI, nbThreadsNetwork, false);
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
	}

	/**
	 * This function schedules the connection and disconnection of a node to a
	 * network based on a given
	 * start instant and delay.
	 */
	private void scheduleTasks() throws Exception {

		// connexion à l'horloge
		this.doPortConnection(this.csop.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());

		AcceleratedClock clock = this.csop.getClock(ConnectionDisconnectionScenario.CLOCK_URI);
		// recuperation de la date du scenario
		Instant startInstant = clock.getStartInstant();

		// synchronisaiton: tous les noeuds doivent patienter jusqu'à la date
		// du rendez-vous: (startInstant)
		clock.waitUntilStart();

		int delay = Helpers.getRandomNumber(4);
		long delayInNanosToJoin = clock.nanoDelayUntilAcceleratedInstant(startInstant.plusSeconds(4 + delay));

		long delayInNanosToLeave = clock
				.nanoDelayUntilAcceleratedInstant(startInstant.plusSeconds(15));

		scheduleConnectionToNetwork(delayInNanosToJoin);
		debugPrinter.display("[node join network] has been scheduled");
		scheduleDisconnectionToNetwork(delayInNanosToLeave);
		debugPrinter.display("[node disconnection] has been scheduled");

	}

	private void scheduleDisconnectionToNetwork(long delayInNanosToLeave) throws AssertionError {
		this.scheduleTask(o -> {
			try {
				plugin.leaveNetwork();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delayInNanosToLeave, TimeUnit.NANOSECONDS);
	}

	private void scheduleConnectionToNetwork(long delayInNanosToJoin) throws AssertionError {
		this.scheduleTask(o -> {
			try {
				plugin.joinNetwork();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delayInNanosToJoin, TimeUnit.NANOSECONDS);
	}

	public ContentNode getContentNode() {
		return node;
	}
}