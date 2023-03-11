package scenarios.match;

import components.Client;

public class ClientLookingForContentWhichMatch extends Client {

	protected ClientLookingForContentWhichMatch(String reflectionInboundPort, String CMNodeManagementInboundURI)
			throws Exception {
		super(reflectionInboundPort, CMNodeManagementInboundURI);
	}

	@Override
	public void execute() throws Exception {
		exampleSearchContainsWichMatch();
		super.execute();
	}
}
