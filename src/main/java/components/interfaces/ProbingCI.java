package main.java.components.interfaces;

import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;

public interface ProbingCI extends OfferedCI, RequiredCI {
    void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
            int chosenNeighbourCount) throws RejectedExecutionException, AssertionError, Exception;
}
