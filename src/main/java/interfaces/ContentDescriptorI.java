package main.java.interfaces;

public interface ContentDescriptorI extends ContentTemplateI {
  /**
   * @return The address of the content node.
   */
  ContentManagementNodeAddressI getContentNodeAdressI();

  /**
   * @return The size of the file in bytes.
   */
  long getSize();

  /**
   * Returns true if the content descriptor is equal to the given content
   * descriptor
   * 
   * @param cd The ContentDescriptorI object to compare to.
   * @return A boolean value.
   */
  boolean equals(ContentDescriptorI cd);

  /**
   * > Returns true if the given template matches the current template
   * 
   * @param t The template to match against.
   * @return A boolean value.
   */
  boolean match(ContentTemplateI t);
}
