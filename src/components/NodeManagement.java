package components;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import plugins.ContentManagement.FacadeContentManagement.FacadeContentManagementPlugin;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import plugins.NetworkFacade.NodeManagementPlugin;
import plugins.NetworkScanner.NetworkScannerPlugin;
import interfaces.ContentNodeAddressI;
import interfaces.FacadeNodeAddressI;

public class NodeManagement extends AbstractComponent implements FacadeNodeAddressI, ContentNodeAddressI {

	private NodeManagementPlugin plugin;

	protected ClocksServerOutboundPort csop;

	protected NodeManagement(String reflectionInboundPortURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 8, 0);

		FacadeContentManagementPlugin ContentManagementPlug = new FacadeContentManagementPlugin(DescriptorId, this);
		this.installPlugin(ContentManagementPlug);

		NetworkScannerPlugin NetworkScannerPlug = new NetworkScannerPlugin(ContentManagementPlug);
		this.installPlugin(NetworkScannerPlug);

		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();

		plugin = new NodeManagementPlugin(ContentManagementPlug, NetworkScannerPlug);
		this.installPlugin(plugin);
	}


	@Override
	public void execute() throws Exception {
		scheduleTasks();
	}

	private void scheduleTasks() throws Exception {

		// connexion à l'horloge
		this.doPortConnection(this.csop.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());

		AcceleratedClock clock = this.csop.getClock(scenarios.connect_disconnect.ConnectionDisconnectionScenario.CLOCK_URI);
		// recuperation de la date du scenario
		Instant startInstant = clock.getStartInstant();

		// synchronisaiton: tous les noeuds doivent patienter jusqu'à la date
		// du rendez-vous: (startInstant)
		clock.waitUntilStart();

		int delay = new Random().nextInt(3);
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
	
	@Override
	public boolean isFacade() {
		return true;
	}

	@Override
	public boolean isPeer() {
		return false;
	}

	@Override
	public String getNodeIdentifier() throws Exception {
		return this.plugin.getPluginURI();
	}

	@Override
	public String getNodeManagementURI() {
		return reflectionInboundPortURI;
	}

	@Override
	public String getContentManagementURI() {
		return "cm-" + reflectionInboundPortURI;
	}

	@Override
	public String getNodeURI() throws Exception {
		throw new UnsupportedOperationException("Unimplemented method 'getNodeURI'");
	}
}
