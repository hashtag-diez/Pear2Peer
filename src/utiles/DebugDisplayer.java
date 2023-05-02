package utiles;

import fr.sorbonne_u.components.AbstractComponent;

/**
 * The DebugDisplayer class displays a message if debugging is enabled.
 * Can eventually evolve to display the message in a file.
 */
public class DebugDisplayer {
	protected boolean debuging = false;

	public DebugDisplayer(boolean debuging) {
		this.debuging = debuging;
	}

	public void trace(String msg, AbstractComponent component) {
		if (debuging) {
			component.traceMessage(msg + "\n\n");
		}
	}

	public void display(String msg) {
		if (debuging) {
			System.out.println(msg);
		}
	}

}
