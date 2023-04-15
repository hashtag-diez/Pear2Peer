package components;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import components.interfaces.NodeCI;
import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import plugins.ContentManagement.ContentManagementPlugin;
import plugins.NetworkNode.NodePlugin;
import plugins.NetworkScanner.NetworkScannerPlugin;
import interfaces.ContentNodeAddressI;
import scenarios.connect_disconnect.ConnectionDisconnectionScenario;
import utiles.Displayer;

@RequiredInterfaces(required = { NodeManagementCI.class, NodeCI.class })
@OfferedInterfaces(offered = { NodeCI.class })
public class Node extends AbstractComponent implements ContentNodeAddressI {

	private static final boolean DEBUG_MODE = true;

	protected ClocksServerOutboundPort csop;

	private NodePlugin plugin;

	protected Node(String reflectionInboundPortURI, String NMInboundURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 1, 1);

		ContentManagementPlugin ContentManagementPlug = new ContentManagementPlugin(DescriptorId, this);
		this.installPlugin(ContentManagementPlug);

		NetworkScannerPlugin NetworkScannerPlug = new NetworkScannerPlugin(ContentManagementPlug);
		this.installPlugin(NetworkScannerPlug);

		plugin = new NodePlugin(NMInboundURI, ContentManagementPlug, NetworkScannerPlug);
		this.installPlugin(plugin);

		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
	}

	@Override
	public void execute() throws Exception {
		scheduleTasks();
	}

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

		long delayInNanosToJoin = clock.nanoDelayUntilAcceleratedInstant(startInstant.plusSeconds(1));

		long delayInNanosToLeave = clock.nanoDelayUntilAcceleratedInstant(startInstant.plusSeconds(7));

		scheduleConnectionToNetwork(delayInNanosToJoin);
		Displayer.display("[node join network] has been scheduled", DEBUG_MODE);
		scheduleDisconnectionToNetwork(delayInNanosToLeave);
		Displayer.display("[node disconnection] has been scheduled", DEBUG_MODE);

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

	@Override
	public boolean isFacade() {
		return false;
	}

	@Override
	public boolean isPeer() {
		return true;
	}

	@Override
	public String getNodeIdentifier() throws Exception {
		return plugin.getNodeInboundPort().getPortURI();
	}

	@Override
	public String getNodeURI() {
		return reflectionInboundPortURI;
	}

	@Override
	public String getContentManagementURI() {
		return "cm-" + reflectionInboundPortURI;
	}
}