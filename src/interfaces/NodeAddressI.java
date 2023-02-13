package interfaces;

import fr.sorbonne_u.utils.Pair;

public interface NodeAddressI {
  boolean isFacade();
  boolean isPeer();
  Pair<String, String> getNodeIdentifier() throws Exception;
}
