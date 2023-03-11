package scenarios.find;

import components.Client;
import utiles.Displayer;

public class ClientLookingForContent extends Client {

	private static final boolean DEBUG_MODE = false;

	protected ClientLookingForContent(String reflectionInboundPort, String CMNodeManagementInboundURI)
			throws Exception {
		super(reflectionInboundPort, CMNodeManagementInboundURI);
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		//super.execute();
		Displayer.display("scenario 1:", DEBUG_MODE);
		//exampleSearchContainsWichMatch();
		exampleSearchFind();
	}
}
