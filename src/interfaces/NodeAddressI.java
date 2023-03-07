package interfaces;

import plugins.PluginOwnerI;

public interface NodeAddressI extends PluginOwnerI {
  boolean isFacade();

  boolean isPeer();

  String getNodeIdentifier() throws Exception;
}
