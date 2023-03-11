package ports;

import java.util.Set;

import components.interfaces.ClientCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ContentDescriptorI;

public class ClientOutboundPort extends AbstractOutboundPort implements ClientCI {

    public ClientOutboundPort(ComponentI owner) throws Exception {
        super(generatePortURI(), ClientCI.class, owner);

    }

    @Override
    public void findResult(ContentDescriptorI result) throws Exception {
        ((ClientCI) this.getConnector()).findResult(result);
    }

    @Override
    public void matchResult(Set<ContentDescriptorI> result) throws Exception {
        ((ClientCI) this.getConnector()).matchResult(result);
    }

}
