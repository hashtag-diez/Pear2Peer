package main.java.utiles;

import java.util.HashSet;
import java.util.Set;

import main.java.interfaces.PeerNodeAddressI;
import main.java.plugins.FacadeContentManagement.port_connector.FacadeContentManagementOutboundPort;

/**
 * The AsyncProbe class keeps track of missing data and received data from an
 * asynchronous probe.
 */

public class AsyncCollector {
    protected int nbMissing;
    protected long startTime;
    protected Set<Object> received = new HashSet<Object>();

    public AsyncCollector(int nbMissing) {
        if (nbMissing < 0)
            throw new IllegalArgumentException("Invalid Collector");

        this.nbMissing = nbMissing;
        startTime = System.currentTimeMillis();
    }

    public void retrieve(Object node) {
        if (isComplete())
            throw new IllegalStateException("Collector already completed");
        received.add(node);
        nbMissing--;
    }

    public boolean isComplete() {
        return nbMissing <= 0;
    }

    public Set<Object> getResult() {
        assert isComplete();
        return received;
    }

    public Set<PeerNodeAddressI> getProbeResult() {
        Set<Object> result = getResult();
        Set<PeerNodeAddressI> res = new HashSet<>();
        for (Object o : result) {
            assert o instanceof PeerNodeAddressI;
            res.add((PeerNodeAddressI) o);
        }
        return res;
    }

    public FacadeContentManagementOutboundPort getTargetFacadePort() {
        Set<Object> result = getResult();
        return (FacadeContentManagementOutboundPort) Helpers.getRandomElement(result);
    }

}
