package interfaces;

public interface ContentDescriptorI extends ContentTemplateI{
  ContentNodeAddressI getContentNodeAdressI();
  long getSize();
  boolean equals(ContentDescriptorI cd) throws Exception;
  boolean match(ContentTemplateI t);
}
