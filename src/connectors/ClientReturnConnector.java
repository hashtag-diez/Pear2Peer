package connectors;

import java.util.Set;

import components.interfaces.ClientCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ContentDescriptorI;

public class ClientReturnConnector
    extends AbstractConnector
    implements ClientCI {

  @Override
  public void findResult(ContentDescriptorI result) throws Exception {
    ((ClientCI) this.offering).findResult(result);
  }

  @Override
  public void matchResult(Set<ContentDescriptorI> result) throws Exception {
    ((ClientCI) this.offering).matchResult(result);
  }

}
