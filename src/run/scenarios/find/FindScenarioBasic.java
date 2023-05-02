package run.scenarios.find;

import fr.sorbonne_u.components.AbstractComponent;
import run.scenarios.SearchScenario;

public class FindScenarioBasic extends SearchScenario {

	public FindScenarioBasic() throws Exception {
		super();
	}

	@Override
	protected void createClientComponent() throws Exception {
		AbstractComponent.createComponent(
				ClientLookingForContent.class.getCanonicalName(),
				new Object[] { CLIENT_COMPONENT_URI,
						NODE_MANAGEMENT_COMPONENT_URI + "-" + 1, getX(), getY() });
		this.toggleTracing(CLIENT_COMPONENT_URI);
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			FindScenarioBasic a = new FindScenarioBasic();
			// Execute the application.
			a.startStandardLifeCycle(20000L);
			// Give some time to see the traces (convenience).
			Thread.sleep(100000L);
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
