package interfaces;

public interface ContentDescriptorI extends ContentTemplateI{
  ContentNodeAddressI getContentNodeAdressI();
  long getSize();
  boolean equals(ContentDescriptorI cd);
  boolean match(ContentTemplateI t);
}
