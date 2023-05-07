package main.java.implem;

import main.java.interfaces.ContentNodeAddressI;

public class ContentNode implements ContentNodeAddressI {

  private String ContentManagementURI;
  private String Id;
  private String NodeURI;

  public ContentNode(String NodeURI, String ContentManagementURI, String Id) {
    this.NodeURI = NodeURI;
    this.ContentManagementURI = ContentManagementURI;
    this.Id = Id;
  }

  @Override
  public String getContentManagementURI() {
    return ContentManagementURI;
  }

  @Override
  public String getNodeIdentifier() {
    return Id;
  }

  @Override
  public boolean isFacade() {
    return false;
  }

  @Override
  public boolean isPeer() {
    return true;
  }

  @Override
  public String getNodeURI() {
    return NodeURI;
  }

}
