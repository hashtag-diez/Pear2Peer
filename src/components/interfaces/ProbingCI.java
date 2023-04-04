package components.interfaces;

import java.util.concurrent.RejectedExecutionException;

import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;

public interface ProbingCI {
    void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI requester) throws RejectedExecutionException, AssertionError, Exception;
}