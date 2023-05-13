package main.java.implem;

import java.io.Serializable;

import main.java.interfaces.ApplicationNodeAddressI;

public class ApplicationNode implements ApplicationNodeAddressI, Serializable {

  private String ContentManagementURI;
  private String Id;
  private String NodeManagementURI;

  public ApplicationNode(String NodeManagementURI, String ContentManagementURI, String Id) {
    this.NodeManagementURI = NodeManagementURI;
    this.ContentManagementURI = ContentManagementURI;
    this.Id = Id;
  }

  @Override
  public String getNodeManagementURI() {
    return NodeManagementURI;
  }

  @Override
  public boolean isFacade() {
    return true;
  }

  @Override
  public boolean isPeer() {
    return false;
  }

  @Override
  public String getNodeIdentifier() {
    return Id;
  }

  @Override
  public String getContentManagementURI() {
    return ContentManagementURI;
  }

}
