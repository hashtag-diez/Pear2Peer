package utiles;

import java.util.HashSet;
import java.util.Set;

import interfaces.PeerNodeAddressI;

/**
 * The AsyncProbe class keeps track of missing data and received data from an
 * asynchronous probe.
 */

public class AsyncProbe {
    protected int nbMissing;
    protected Set<PeerNodeAddressI> received = new HashSet<PeerNodeAddressI>();

    public AsyncProbe(int nbMissing) {
        this.nbMissing = nbMissing;
    }

    public void retrieve(PeerNodeAddressI node) {
        received.add(node);
        nbMissing--;
    }

    public boolean isComplete() {
        return nbMissing == 0;
    }

    public Set<PeerNodeAddressI> getResult() {
        assert isComplete();
        return received;
    }
}
