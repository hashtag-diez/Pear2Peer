package components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import components.interfaces.ClientCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import implem.ContentTemplate;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.port_connector.ContentManagementOutboundPort;
import plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import plugins.FacadeContentManagement.FacadeContentManagementPI;
import plugins.NetworkScanner.NetworkScannerPI;
import plugins.NetworkScanner.port_connector.NetworkScannerOutboundPort;
import plugins.NetworkScanner.port_connector.NetworkScannerServiceConnector;
import ports.ClientInboundPort;
import utiles.DebugDisplayer;
import utiles.Helpers;

@OfferedInterfaces(offered = { ClientCI.class })
@RequiredInterfaces(required = { ContentManagementPI.class, NetworkScannerPI.class })
public class Client extends AbstractComponent {

	protected ClientInboundPort ReturnPort;
	// The port used to call the methods of the ContentManagementPI.
	protected ContentManagementOutboundPort CMGetterPort;
	// The port used to call the methods of the NetworkScannerPI.
	protected NetworkScannerOutboundPort NSGetterPort;
	protected String NodeManagementURI;
	protected boolean found = false;
	private static final boolean DEBUG_MODE = true;
	private DebugDisplayer debugPrinter = new DebugDisplayer(DEBUG_MODE);

	// The constructor of the Client class. It creates the Client object and
	// initializes the ports.
	protected Client(String reflectionInboundPort, String NodeManagementURI) throws Exception {
		super(reflectionInboundPort, 1, 1);
		this.CMGetterPort = new ContentManagementOutboundPort(this);
		this.CMGetterPort.publishPort();
		this.NodeManagementURI = NodeManagementURI;

		this.NSGetterPort = new NetworkScannerOutboundPort(this);
		this.NSGetterPort.publishPort();

		this.ReturnPort = new ClientInboundPort(this);
	}

	/**
	 * It connects the two ports of the component to the two ports of the two
	 * services
	 */
	@Override
	public void start() throws ComponentStartException {
		try {
			super.start();
			ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
			rop.publishPort();
			connectToFacadeViaCM(rop);
			connectToFacadeViaNS(rop);
			this.doPortDisconnection(rop.getPortURI());
			rop.unpublishPort();
			rop.destroyPort();
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();
	}

	private void connectToFacadeViaCM(ReflectionOutboundPort rop) throws Exception {

		this.doPortConnection(rop.getPortURI(), NodeManagementURI, ReflectionConnector.class.getCanonicalName());

		String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(FacadeContentManagementPI.class);
		if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
			debugPrinter.display("NOPE");
		} else {
			this.doPortConnection(CMGetterPort.getPortURI(), otherInboundPortUI[0],
					ContentManagementServiceConnector.class.getCanonicalName());
		}
	}

	private void connectToFacadeViaNS(ReflectionOutboundPort rop) throws Exception {

		this.doPortConnection(rop.getPortURI(), NodeManagementURI, ReflectionConnector.class.getCanonicalName());

		String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NetworkScannerPI.class);
		if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
			debugPrinter.display("NOPE");
		} else {
			this.doPortConnection(NSGetterPort.getPortURI(), otherInboundPortUI[0],
					NetworkScannerServiceConnector.class.getCanonicalName());
		}
	}

	/**
	 * It reads the templates from the data directory, picks a random one, and
	 * returns it
	 * 
	 * @return A ContentTemplate object
	 */
	public ContentTemplateI pickTemplate() throws ClassNotFoundException, IOException {
		ContentDataManager.DATA_DIR_NAME = "src/data2";
		ArrayList<HashMap<String, Object>> result = ContentDataManager.readTemplates(0);
		return new ContentTemplate(Helpers.getRandomElement(result));
	}

	/**
	 * It picks a template, prints it, and then asks the Network for matches
	 */
	public void exampleSearchContainsWichMatch() throws Exception {
		debugPrinter.display("Client start searching [match]");
		ContentTemplateI temp = pickTemplate();
		debugPrinter.display("Template recherche :\\n" + temp);
		Set<ContentDescriptorI> matched = new HashSet<>();

		ReturnPort.publishPort();
		CMGetterPort.match(temp, matched, 5, null, ReturnPort.getPortURI());
		debugPrinter.display("Matched count: " + matched.size());
	}

	/**
	 * It picks a template, prints it, and then asks the Network to find it
	 */
	public void exampleSearchFind() throws Exception {
		debugPrinter.display("Client start searching [find]");
		ContentTemplateI temp = pickTemplate();
		debugPrinter.display("Template recherche :\n" + temp.toString());
		found = false;
		ReturnPort.publishPort();
		System.out.println("\t\t\t\t\t\t " + ReturnPort.getPortURI());
		CMGetterPort.find(temp, 4, null, ReturnPort.getPortURI());
	}

	/**
	 * If the result has not been found, print the result, otherwise set the result
	 * to found.
	 * 
	 * @param matched The ContentDescriptorI object that matched the search
	 *                criteria.
	 * @throws Exception
	 */
	public synchronized void findResult(ContentDescriptorI matched) throws Exception {
		if (ReturnPort.isPublished()) {
			if (found == false) {
				ReturnPort.unpublishPort();
				debugPrinter.display("Found : " + matched.toString());
			} else
				found = true;
		}

	}

	/**
	 * If the set of matched content descriptors is not empty, print out the matched
	 * content descriptors
	 * 
	 * @param matched A set of ContentDescriptorI objects that matched the query.
	 */
	public synchronized void matchResult(Set<ContentDescriptorI> matched) throws Exception {
		if (ReturnPort.isPublished()) {
			if (!matched.isEmpty()) {
				ReturnPort.unpublishPort();
				debugPrinter.display("Matched : ");
				for (ContentDescriptorI contentDescriptor : matched) {
					debugPrinter.display(contentDescriptor.toString());
				}
			}
		}
	}

}
