package ports;

import java.util.Set;

import components.interfaces.ClientCI;
import fr.sorbonne_u.components.ComponentI;
import components.Client;

import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ContentDescriptorI;

public class ClientInboundPort extends AbstractInboundPort implements ClientCI {

    public ClientInboundPort(ComponentI owner) throws Exception {
        super(generatePortURI(), ClientCI.class, owner);
    }

    @Override
    public void findResult(ContentDescriptorI result) throws Exception {
        this.getOwner().runTask(
                owner -> {
                    try {
                        ((Client) owner).findResult(result);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void matchResult(Set<ContentDescriptorI> result) throws Exception {
        this.getOwner().runTask(
                owner -> {
                    try {
                        ((Client) owner).matchResult(result);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
