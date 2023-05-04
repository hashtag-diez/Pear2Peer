package main.java.plugins.NetworkScanner;

import java.util.List;
import java.util.Set;

import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.NodeAddressI;

public interface NodeInformationI {
    boolean isFacade();

    boolean isPeer();

    Set<NodeAddressI> getConnectedNodes();

    List<ContentDescriptorI> getContents();
    /* Other Information to add */
}