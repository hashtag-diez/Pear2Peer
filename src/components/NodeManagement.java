package components;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import implem.ApplicationNode;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import plugins.FacadeContentManagement.FacadeContentManagementPlugin;
import plugins.NetworkFacade.NodeManagementPlugin;
import plugins.NetworkScanner.NetworkScannerPlugin;

@RequiredInterfaces(required = { ClocksServerCI.class })
public class NodeManagement extends AbstractComponent {

	private NodeManagementPlugin plugin;

	protected ClocksServerOutboundPort csop;

	private ApplicationNode app;
	private static final int DEFAULT_NB_OF_THREADS = 8;

	private static final String NM_EXECUTION_SERVICE_URI = "app-networkmanagement-tasks-execution-service";
	private static final String CM_EXECUTION_SERVICE_URI = "app-content-tasks-execution-service";

	protected NodeManagement(String reflectionInboundPortURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, DEFAULT_NB_OF_THREADS, DEFAULT_NB_OF_THREADS);
		this.initialise(DEFAULT_NB_OF_THREADS);

		String NodeManagementURI = AbstractPort.generatePortURI();
		String ContentManagementURI = AbstractPort.generatePortURI();
		app = new ApplicationNode(NodeManagementURI, ContentManagementURI, reflectionInboundPortURI);

		FacadeContentManagementPlugin ContentManagementPlug = new FacadeContentManagementPlugin(ContentManagementURI, DescriptorId, app);
		ContentManagementPlug.setPreferredExecutionServiceURI(CM_EXECUTION_SERVICE_URI);
		this.installPlugin(ContentManagementPlug);

		NetworkScannerPlugin NetworkScannerPlug = new NetworkScannerPlugin(ContentManagementPlug);
		this.installPlugin(NetworkScannerPlug);

		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();

		plugin = new NodeManagementPlugin(NodeManagementURI, ContentManagementPlug, NetworkScannerPlug);
		plugin.setPreferredExecutionServiceURI(NM_EXECUTION_SERVICE_URI);
		this.installPlugin(plugin);
	}

	protected void initialise(int nbThreads) {
		assert nbThreads >= 4 : "Contrainte sur le nombre de threads [" + DEFAULT_NB_OF_THREADS + "]";
		int nbThreadsNetwork = 4;
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

	private void scheduleTasks() throws Exception {

		// connexion à l'horloge
		this.doPortConnection(this.csop.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());

		AcceleratedClock clock = this.csop
				.getClock(scenarios.connect_disconnect.ConnectionDisconnectionScenario.CLOCK_URI);
		// recuperation de la date du scenario
		Instant startInstant = clock.getStartInstant();

		// synchronisaiton: tous les noeuds doivent patienter jusqu'à la date
		// du rendez-vous: (startInstant)
		clock.waitUntilStart();

		int delay = new Random().nextInt(1);
		long delayInNanosToJoin = clock.nanoDelayUntilAcceleratedInstant(startInstant.plusSeconds(delay));

		scheduleConnectionWithFacades(delayInNanosToJoin);
	}

	private void scheduleConnectionWithFacades(long delayInNanosToJoin) throws AssertionError {
		this.scheduleTask(o -> {
			try {
				this.plugin.connectWithFacade();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delayInNanosToJoin, TimeUnit.NANOSECONDS);
	}
	public ApplicationNode getApplicationNode(){
		return app;
	}
}
