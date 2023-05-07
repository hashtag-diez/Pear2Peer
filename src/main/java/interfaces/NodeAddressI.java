package main.java.interfaces;

public interface NodeAddressI {
  boolean isFacade();

  boolean isPeer();

  /**
   * This function returns a string representing the identifier of a node.
   * The identifier is unique for each node.
   * 
   * @return A string representing the identifier of a node.
   */
  String getNodeIdentifier();
}
