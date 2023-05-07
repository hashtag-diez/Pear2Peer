package main.java.components.interfaces;

import main.java.interfaces.FacadeNodeAddressI;
import main.java.interfaces.PeerNodeAddressI;

public interface ProbingCI {
    /**
     * This function takes in various parameters and performs some actions related
     * to network
     * communication.
     * 
     * @param requestURI           URI of the requesting node.
     * @param facade               The facade processing the probing request.
     * @param remainingHops        Represents the number of
     *                             remaining hops or nodes that the request needs to
     *                             traverse before reaching its destination. It
     *                             is used to limit the number of hops a request can
     *                             make to prevent infinite loops and ensure
     *                             efficient routing.
     * @param chosen               Represents the chosen peer
     *                             node address for the current request.
     * @param chosenNeighbourCount
     *                             Represents the number of neighbors of the chosen
     *                             peer node.
     *                             It is used to determine if the chosen peer node
     *                             is the best choice for the current request.
     * @throws Exception
     * 
     */
    void probe(String requestURI, FacadeNodeAddressI facade, int remainingHops, PeerNodeAddressI chosen,
            int chosenNeighbourCount) throws Exception;
}
