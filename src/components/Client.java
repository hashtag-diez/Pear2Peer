package components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

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
import interfaces.NodeAddressI;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.port_connector.CMOutboundPort;
import plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import plugins.NetworkScanner.NetworkScannerPI;
import plugins.NetworkScanner.NodeInformationI;
import plugins.NetworkScanner.port_connector.NSPoutBoundPort;
import plugins.NetworkScanner.port_connector.NetworkScannerServiceConnector;
import ports.ClientInboundPort;
import utiles.Displayer;

@OfferedInterfaces(offered = { ClientCI.class })
@RequiredInterfaces(required = { ContentManagementPI.class, NetworkScannerPI.class })
public class Client extends AbstractComponent {

	private static final boolean DEBUG_MODE = false;
	protected ClientInboundPort ReturnPort;
	// The port used to call the methods of the ContentManagementPI.
	protected CMOutboundPort CMGetterPort;
	// The port used to call the methods of the NetworkScannerPI.
	protected NSPoutBoundPort NSGetterPort;
	protected String NodeManagementURI;
	protected boolean found = false;

	// The constructor of the Client class. It creates the Client object and
	// initializes the ports.
	protected Client(String reflectionInboundPort, String NodeManagementURI) throws Exception {
		super(reflectionInboundPort, 1, 0);
		this.CMGetterPort = new CMOutboundPort(this);
		this.CMGetterPort.publishPort();
		this.NodeManagementURI = NodeManagementURI;

		this.NSGetterPort = new NSPoutBoundPort(this);
		this.NSGetterPort.publishPort();

		this.ReturnPort = new ClientInboundPort(this);
		this.ReturnPort.publishPort();
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
		Displayer.display("waiting 2 seconds", DEBUG_MODE);
		// mapNetwork();
		// System.out.println("Map network");
		// exampleSearchFind();
	}

	private void connectToFacadeViaCM(ReflectionOutboundPort rop) throws Exception {

		this.doPortConnection(rop.getPortURI(), NodeManagementURI, ReflectionConnector.class.getCanonicalName());

		String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(ContentManagementPI.class);
		if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
			Displayer.display("NOPE", DEBUG_MODE);
		} else {
			this.doPortConnection(CMGetterPort.getPortURI(), otherInboundPortUI[0],
					ContentManagementServiceConnector.class.getCanonicalName());
		}
	}

	private void connectToFacadeViaNS(ReflectionOutboundPort rop) throws Exception {

		this.doPortConnection(rop.getPortURI(), NodeManagementURI, ReflectionConnector.class.getCanonicalName());

		String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(NetworkScannerPI.class);
		if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
			Displayer.display("NOPE", DEBUG_MODE);
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
		ContentDataManager.DATA_DIR_NAME = "src/data";
		ArrayList<HashMap<String, Object>> result = ContentDataManager.readTemplates((int) Math.random() % 2);
		HashMap<String, Object> random = result.get((int) Math.random() % result.size());
		return new ContentTemplate(random);
	}

	/**
	 * It picks a template, prints it, and then asks the Network for matches
	 */
	public void exampleSearchContainsWichMatch() throws Exception {
		Displayer.display("Client start searching [match]", DEBUG_MODE);
		ContentTemplateI temp = pickTemplate();
		Displayer.display("Template recherche :\\n"+temp, DEBUG_MODE);
		Set<ContentDescriptorI> matched = new HashSet<>();

		CMGetterPort.match(temp, matched, 5, ReturnPort.getPortURI());
		Displayer.display("Matched count: " + matched.size(), DEBUG_MODE);
	}

	/**
	 * It picks a template, prints it, and then asks the Network to find it
	 */
	public void exampleSearchFind() throws Exception {
		Displayer.display("Client start searching [find]", DEBUG_MODE);
		ContentTemplateI temp = pickTemplate();
		Displayer.display("Template recherche :\n" + temp.toString(), DEBUG_MODE);
		found = false;
		CMGetterPort.find(temp, 5, ReturnPort.getPortURI());
	}

	/**
	 * If the result has not been found, print the result, otherwise set the result
	 * to found.
	 * 
	 * @param matched The ContentDescriptorI object that matched the search
	 *                criteria.
	 */
	public void findResult(ContentDescriptorI matched) {
		if (found == false) {
			Displayer.display("Found : " + matched.toString(), DEBUG_MODE);
		} else
			found = true;
	}

	/**
	 * If the set of matched content descriptors is not empty, print out the matched
	 * content descriptors
	 * 
	 * @param matched A set of ContentDescriptorI objects that matched the query.
	 */
	public void matchResult(Set<ContentDescriptorI> matched) {
		if (!matched.isEmpty()) {
			Displayer.display("Matched : ", DEBUG_MODE);
			for (ContentDescriptorI contentDescriptor : matched) {
				Displayer.display(contentDescriptor.toString(), DEBUG_MODE);
			}
		}
	}

	/**
	 * It gets the network map from the NSGetterPort, and prints it
	 */
	public void mapNetwork() throws Exception {
		HashMap<NodeAddressI, NodeInformationI> result = new HashMap<>();
		result = NSGetterPort.mapNetwork(result);
		Displayer.display("Contain " + result.size() + " Nodes",DEBUG_MODE);
		for (Entry<NodeAddressI, NodeInformationI> nodeInfo : result.entrySet()) {
			Displayer.display("Node " + nodeInfo.getKey().getNodeIdentifier() + " : ", DEBUG_MODE);
			Displayer.display(nodeInfo.getValue().toString(), DEBUG_MODE);
			Displayer.display("--------------------------------", DEBUG_MODE);
		}
	}
}
