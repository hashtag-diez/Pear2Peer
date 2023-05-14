package main.java.components;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.components.helpers.Logger;
import fr.sorbonne_u.components.helpers.TracerWindow;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import main.java.implem.ContentNode;
import main.java.plugins.ContentManagement.ContentManagementPlugin;
import main.java.plugins.NetworkNode.NodePlugin;
import main.java.utiles.DebugDisplayer;
import main.java.utiles.Helpers;

@RequiredInterfaces(required = { ClocksServerCI.class })
public class Node extends AbstractComponent {

	protected ClocksServerOutboundPort csop;

	protected ContentNode node;

	private NodePlugin plugin;

	/** Execution log of the cyclic barrier.								*/
	protected final Logger					executionLog;
	/** 	Tracer of the cyclic barrier.									*/
	protected final TracerWindow			tracer;

	private static final int DEFAULT_NB_OF_THREADS = 8;
	private static final boolean DEBUG_MODE = true;
	protected DebugDisplayer debugPrinter = new DebugDisplayer(DEBUG_MODE);

	private static final String NM_EXECUTION_SERVICE_URI = "networkmanagement-tasks-execution-service";
	private static final String CM_EXECUTION_SERVICE_URI = "content-tasks-execution-service";

	protected Node(String reflectionInboundPortURI, String NMInboundURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, DEFAULT_NB_OF_THREADS, DEFAULT_NB_OF_THREADS);
		this.initialise(DEFAULT_NB_OF_THREADS);

		this.tracer = new TracerWindow();	
		this.executionLog = new Logger(reflectionInboundPortURI);
		// tracer.toggleTracing();
		// executionLog.toggleLogging();
		String NodeURI = AbstractPort.generatePortURI();
		String ContentManagementURI = AbstractPort.generatePortURI();
		node = new ContentNode(NodeURI, ContentManagementURI, reflectionInboundPortURI);
		Integer NodeIndex = Integer.parseInt(reflectionInboundPortURI.split("_")[2]);

		ContentManagementPlugin ContentManagementPlug = new ContentManagementPlugin(ContentManagementURI, DescriptorId,
				node);
		ContentManagementPlug.setPreferredExecutionServiceURI(CM_EXECUTION_SERVICE_URI+"-"+NodeIndex.toString());
		this.installPlugin(ContentManagementPlug);

		plugin = new NodePlugin(NMInboundURI, NodeURI, ContentManagementPlug);
		plugin.setPreferredExecutionServiceURI(NM_EXECUTION_SERVICE_URI+"-"+NodeIndex.toString());
		this.installPlugin(plugin);

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
		int nbThreadsNetwork = 5;
		int nbThreadsContent = nbThreads - nbThreadsNetwork;

		// this.createNewExecutorService(NS_EXECUTION_SERVICE_URI, nbThreadsNetwork,
		// false);
		Integer NodeIndex = Integer.parseInt(reflectionInboundPortURI.split("_")[2]);

		this.createNewExecutorService(CM_EXECUTION_SERVICE_URI+"-"+NodeIndex.toString(), nbThreadsContent, true);
		this.createNewExecutorService(NM_EXECUTION_SERVICE_URI+"-"+NodeIndex.toString(), nbThreadsNetwork, true);
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

		Set<String>	 URIs = new HashSet<>(this.portURIs2ports.keySet());
		for(String uri : URIs){
			PortI port = this.portURIs2ports.get(uri);
			try{
				if(port.connected()){
					this.doPortDisconnection(port.getPortURI());
				}
			} catch(ConnectionException e){
				
			} finally{
				if(port.isPublished()) port.unpublishPort();
				if(!port.isDestroyed()) port.destroyPort();
			}
		}
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

		AcceleratedClock clock = this.csop.getClock(Helpers.GLOBAL_CLOCK_URI);
		// recuperation de la date du scenario

		// synchronisaiton: tous les noeuds doivent patienter jusqu'à la date
		// du rendez-vous: (startInstant)
		clock.waitUntilStart();

		Instant start = clock.getStartInstant();
		int delay = Helpers.getRandomNumber(4);

		long delayInNanosToLeave = clock.nanoDelayUntilAcceleratedInstant(start.plusSeconds(17));

		scheduleConnectionToNetwork(2 + delay);
		debugPrinter.display("[node join network] has been scheduled");
		scheduleDisconnectionToNetwork(delayInNanosToLeave);
		debugPrinter.display("[node disconnection] has been scheduled");
	}

	private void scheduleDisconnectionToNetwork(long delayInNanosToLeave) throws AssertionError {
		this.scheduleTask(o -> {
			try {
				Thread.sleep(120000);
				plugin.leaveNetwork();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delayInNanosToLeave, TimeUnit.NANOSECONDS);
	}

	private void scheduleConnectionToNetwork(long delayInNanosToJoin) throws AssertionError {
		this.scheduleTask(o -> {
			try {
				Thread.sleep(delayInNanosToJoin*1000);
				plugin.joinNetwork();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delayInNanosToJoin, TimeUnit.NANOSECONDS);
	}

	public ContentNode getContentNode() {
		return node;
	}
	public void writeMessage(String msg){
		System.out.println(msg);
		//this.executionLog.logMessage(msg);
		//this.tracer.traceMessage(System.currentTimeMillis() + "|" + msg +"\n");
	}
}