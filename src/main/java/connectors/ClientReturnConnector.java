package main.java.connectors;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import main.java.components.interfaces.ClientCI;
import main.java.interfaces.ContentDescriptorI;

public class ClientReturnConnector
    extends AbstractConnector
    implements ClientCI {

  @Override
  public void findResult(ContentDescriptorI result, String URI) throws Exception {
    ((ClientCI) this.offering).findResult(result, URI);
  }

  @Override
  public void matchResult(Set<ContentDescriptorI> result, String URI) throws Exception {
    ((ClientCI) this.offering).matchResult(result, URI);
  }

}
