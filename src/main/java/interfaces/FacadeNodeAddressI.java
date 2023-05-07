package main.java.interfaces;

public interface FacadeNodeAddressI extends NodeAddressI {
  /**
   * This function returns a string representing the URI for node management.
   * 
   * @return A string representing the URI (Uniform Resource Identifier) for node
   *         management.
   */
  String getNodeManagementURI();
}
