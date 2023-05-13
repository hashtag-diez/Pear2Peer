package main.java.run.scenarios.find.total_find;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import main.java.components.interfaces.ClientCI;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentTemplateI;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPI;
import main.java.ports.ClientInboundPort;
import main.java.run.scenarios.find.ClientLookingForContent;

@OfferedInterfaces(offered = { ClientCI.class })
@RequiredInterfaces(required = { FacadeContentManagementPI.class, ClocksServerCI.class })
public class ClientLookingForAllContent extends ClientLookingForContent {
	protected static final int HOPS = 3;

	private int ENVOYE = 0;
	private int RECU = 0;
	private HashMap<String, ClientInboundPort> returnPorts = new HashMap<>();
	protected ClientLookingForAllContent(String reflectionInboundPort, String CMNodeManagementInboundURI)
			throws Exception {
		super(reflectionInboundPort, CMNodeManagementInboundURI);
		this.csop = new ClocksServerOutboundPort(this);
		this.csop.publishPort();
	}

	/**
	 * If the result has not been found, print the result, otherwise set the result
	 * to found.
	 * 
	 * @param matched The ContentDescriptorI object that matched the search
	 *                criteria.
	 * @throws Exception
	 */
	@Override
	public synchronized void findResult(ContentDescriptorI matched, String URI) throws Exception {
		if (returnPorts.get(URI)!=null && returnPorts.get(URI).isPublished()) {
			returnPorts.get(URI).unpublishPort();
			returnPorts.get(URI).destroyPort();
			returnPorts.remove(URI);
			// debugPrinter.display("Found : " + matched.toString());
			RECU++;
		}

	}

	/**
	 * If the set of matched content descriptors is not empty, print out the matched
	 * content descriptors
	 * 
	 * @param matched A set of ContentDescriptorI objects that matched the query.
	 */
	@Override
	public synchronized void matchResult(Set<ContentDescriptorI> matched) throws Exception {
		if (ReturnPort.isPublished())
			if (!matched.isEmpty()) {
				ReturnPort.unpublishPort();
				debugPrinter.display("Matched : ");
				for (ContentDescriptorI contentDescriptor : matched)
					debugPrinter.display(contentDescriptor.toString());

			}
		exampleSearchContainsWichMatch();
	}

	@Override
	public void exampleSearchFind() throws Exception {
		ArrayList<ContentTemplateI> list = allTemplates();
		debugPrinter.display("Client start searching every template [find] : " + list.size());
		for(int i = 0; i<list.size()/2;i++){
			// debugPrinter.display("Template recherche :\n" + temp.toString());
			ClientInboundPort port = new ClientInboundPort(this);
			port.publishPort();
			returnPorts.put(port.getPortURI(), port);
			// System.out.println("\t\t\t\t\t\t " + ReturnPort.getPortURI());
			CMGetterPort.find(list.get(i), HOPS, null, port.getPortURI());
			ENVOYE++;
		}
	}
	@Override
	public void finalise() throws Exception{
		System.out.println("ENVOYE : "+ ENVOYE +", RECU : "+ RECU);
		super.finalise();
	}
}
