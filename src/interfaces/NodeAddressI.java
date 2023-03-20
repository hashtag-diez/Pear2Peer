package interfaces;


public interface NodeAddressI  {
  boolean isFacade();

  boolean isPeer();

  String getNodeURI() throws Exception;
}
