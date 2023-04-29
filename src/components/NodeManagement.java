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
import plugins.ContentManagement.FacadeContentManagement.FacadeContentManagementPlugin;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import plugins.NetworkFacade.NodeManagementPlugin;
import plugins.NetworkScanner.NetworkScannerPlugin;

@RequiredInterfaces(required = { ClocksServerCI.class })
public class NodeManagement extends AbstractComponent {

	private NodeManagementPlugin plugin;

	protected ClocksServerOutboundPort csop;

	private ApplicationNode app;

	protected NodeManagement(String reflectionInboundPortURI, int DescriptorId) throws Exception {
		super(reflectionInboundPortURI, 8, 4);

		String NodeManagementURI = AbstractPort.generatePortURI();
		String ContentManagementURI = AbstractPort.generatePortURI();
		app = new ApplicationNode(NodeManagementURI, ContentManagementURI, reflectionInboundPortURI);

		FacadeContentManagementPlugin ContentManagementPlug = new FacadeContentManagementPlugin(NodeManagementURI, DescriptorId, app);
		this.installPlugin(ContentManagementPlug);

		NetworkScannerPlugin NetworkScannerPlug = new NetworkScannerPlugin(ContentManagementPlug);
		this.installPlugin(NetworkScannerPlug);

		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();

		plugin = new NodeManagementPlugin(ContentManagementURI, ContentManagementPlug, NetworkScannerPlug);
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

		AcceleratedClock clock = this.csop
				.getClock(scenarios.connect_disconnect.ConnectionDisconnectionScenario.CLOCK_URI);
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
	public ApplicationNode getApplicationNode(){
		return app;
	}
}
