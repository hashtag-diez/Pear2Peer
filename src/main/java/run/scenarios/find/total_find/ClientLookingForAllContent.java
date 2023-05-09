package main.java.run.scenarios.find.total_find;

import java.util.Set;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import main.java.components.interfaces.ClientCI;
import main.java.interfaces.ContentDescriptorI;
import main.java.plugins.FacadeContentManagement.FacadeContentManagementPI;
import main.java.run.scenarios.find.ClientLookingForContent;

@OfferedInterfaces(offered = { ClientCI.class })
@RequiredInterfaces(required = { FacadeContentManagementPI.class, ClocksServerCI.class })
public class ClientLookingForAllContent extends ClientLookingForContent {
	protected static final int HOPS = 6;

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
	public synchronized void findResult(ContentDescriptorI matched) throws Exception {
		if (ReturnPort.isPublished()) {
			ReturnPort.unpublishPort();
			debugPrinter.display("Found : " + matched.toString());

			exampleSearchFind();
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

}
