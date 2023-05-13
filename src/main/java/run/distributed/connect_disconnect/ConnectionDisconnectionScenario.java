package main.java.run.distributed.connect_disconnect;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import main.java.components.Node;
import main.java.components.NodeManagement;

/**
 * 
 * TUTO : 
 *  - Au préalable, avoir lancé start_cyclebarrier.sh et start_gregistry.sh
 *  - Lancer la JVM avec Run sur Visual Studio Code
 *  - Aller sur le main 
 *  - Taper Run
 *  - Cancel la runtime lancée
 * 	- Supprimer tout ce qu'il y'a après le .jar généré par VSCode
 *  - Mettre à la suite : 
 * 		-Djava.security.manager 
 * 		-Djava.security.policy=dcvm.policy 
 * 		main.java.run.distributed.connect_disconnect.ConnectionDisconnectionScenario 
 * 		my-NODE_MANAGEMENT-1
			config.xml
 * 	- Relancer
 * 
 * Dans ce scenario, chacun des noeuds:
 * - se connecte au reseau,
 * - ensuite fait une tache,
 * - enfin se deconnecte.
 * 
 * La deconnexion ne commence que si tous les noeuds ont fini leur tâche.
 * On programmera la deconnexion à un instant suffisament reculé pour
 * permettre à chacun de determiner sa tâche.
 * 
 * @author aboub_bmdb7gr
 *
 */
public class ConnectionDisconnectionScenario extends AbstractDistributedCVM {

	public ConnectionDisconnectionScenario(String[] args) throws Exception {
		super(args);
		System.out.println(thisJVMURI);
	}

	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my_NODE";
	public static final String CLOCK_URI = "my-clock-uri";

	protected final int NB_PEER = 9;
	/**
	 * Reference to the provider component to share between deploy and shutdown.
	 */
	protected String uriNodeManagement;

	@Override
	public void instantiateAndPublish() throws Exception {
		// decide for a start time as an Instant that will be used as the base
		// time to plan all the actions of the test scenario
		Instant startInstant = Instant.now().plusSeconds(2);
		double accelerationFactor = 1.0;
		ContentDataManager.DATA_DIR_NAME = "src/data";
		Integer FacadeIndex = Integer.parseInt(thisJVMURI.split("-")[2]);

		AbstractComponent.createComponent(NodeManagement.class.getCanonicalName(),
		new Object[] { thisJVMURI, (FacadeIndex-1)*10 });

		for (int i = 1; i <= NB_PEER; i++) {
			AbstractComponent.createComponent(Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI +"_"+ ((FacadeIndex-1)*10 + i), thisJVMURI, i });
		}
		if (thisJVMURI.equals("my-NODE_MANAGEMENT-1")) {
			AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[] { CLOCK_URI, startInstant.toEpochMilli(),
						startInstant, accelerationFactor });
		}
		super.instantiateAndPublish();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			ConnectionDisconnectionScenario a = new ConnectionDisconnectionScenario(args);
			a.startStandardLifeCycle(20000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
