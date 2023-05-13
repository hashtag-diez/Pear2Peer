package main.java.ports;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import main.java.components.interfaces.ClientCI;
import main.java.interfaces.ContentDescriptorI;

public class ClientOutboundPort extends AbstractOutboundPort implements ClientCI {

    public ClientOutboundPort(ComponentI owner) throws Exception {
        super(generatePortURI(), ClientCI.class, owner);

    }

    @Override
    public void findResult(ContentDescriptorI result,String URI) throws Exception {
        ((ClientCI) this.getConnector()).findResult(result, URI);
    }

    @Override
    public void matchResult(Set<ContentDescriptorI> result, String URI) throws Exception {
        ((ClientCI) this.getConnector()).matchResult(result, URI);
    }

}
