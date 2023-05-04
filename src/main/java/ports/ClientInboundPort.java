package main.java.ports;

import java.util.Set;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import main.java.components.Client;
import main.java.components.interfaces.ClientCI;
import main.java.interfaces.ContentDescriptorI;

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
