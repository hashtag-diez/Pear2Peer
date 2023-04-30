package run;

import components.Client;
import components.Node;
import components.NodeManagement;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM
		extends AbstractCVM {

	/** URI of the provider component (convenience). */
	protected static final String NODE_MANAGEMENT_COMPONENT_URI = "my-NODE_MANAGEMENT";
	/** URI of the consumer component (convenience). */
	protected static final String NODE_COMPONENT_URI = "my-NODE";
	/** URI of the consumer inbound port (simplifies the connection). */
	protected static final String NodeManagementInboundPortURI = "NM-iport";

	protected final int NB_PEER = 9;

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		AbstractComponent.createComponent(
				NodeManagement.class.getCanonicalName(),
				new Object[] { NodeManagementInboundPortURI, 0 });

		for (int i = 1; i <= NB_PEER; i++) {
			AbstractComponent.createComponent(
					Node.class.getCanonicalName(),
					new Object[] { NODE_COMPONENT_URI + i,
							NodeManagementInboundPortURI, i });
		}
		AbstractComponent.createComponent(
				Client.class.getCanonicalName(),
				new Object[] { "Client",
				NodeManagementInboundPortURI });

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			CVM a = new CVM();
			// Execute the application.
			a.startStandardLifeCycle(20000L);
			// Give some time to see the traces (convenience).
			Thread.sleep(500L);
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
