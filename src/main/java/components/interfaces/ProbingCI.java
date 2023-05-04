package main.java.components.interfaces;

import java.util.concurrent.RejectedExecutionException;

import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;

public interface ProbingCI {
    void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
            int chosenNeighbourCount) throws RejectedExecutionException, AssertionError, Exception;
}
