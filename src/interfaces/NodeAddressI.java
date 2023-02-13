package interfaces;

public interface NodeAddressI {
  boolean isFacade();
  boolean isPeer();
  String getNodeIdentifier() throws Exception;
}
