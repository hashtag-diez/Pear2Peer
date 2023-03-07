package plugins;

import fr.sorbonne_u.components.PluginI;

public interface PluginOwnerI {
    public enum Plugins {
        ContentManagementPlugin,
        NetworkScannerPlugin
    }

    /**
     * 
     * @param toGet The plugin you wanna to get.
     * @return The plugin that is being requested if installed on component.
     */
    public PluginI getPlugin(Plugins toGet);

    /**
     * This function returns the port of the plugin that is passed in as a parameter
     * 
     * @param portToGet The port you want to get the uri of.
     * @return The port of the plugin.
     */
    public String getPluginPort(Plugins portToGet);
}
