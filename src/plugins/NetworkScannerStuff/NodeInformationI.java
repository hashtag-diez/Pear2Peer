package plugins.NetworkScannerStuff;

import java.util.List;
import java.util.Set;

import interfaces.ContentDescriptorI;
import interfaces.NodeAddressI;

public interface NodeInformationI {
    boolean isFacade();

    boolean isPeer();

    Set<NodeAddressI> getConnectedNodes();

    List<ContentDescriptorI> getContents();
    /* Other Information to add */
}