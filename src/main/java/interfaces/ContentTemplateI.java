package main.java.interfaces;

import java.util.Set;

public interface ContentTemplateI {

  /**
   * This function returns a String representing the title.
   * 
   * @return A string value representing the title.
   */
  String getTitle();

  String getAlbumTitle();

  Set<String> getInterpreters();

  Set<String> getComposers();
}
