package plugins.NetworkScanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import interfaces.ContentDescriptorI;
import interfaces.NodeAddressI;

public class NodeInformation implements NodeInformationI {

    protected boolean isPeer = false;
    protected boolean isFacade = false;
    protected boolean isRoot = false;
    protected Set<NodeAddressI> voisins = new HashSet<NodeAddressI>();
    protected List<ContentDescriptorI> contentsDescriptors;

    public NodeInformation(boolean isPeer, boolean isFacade, Set<NodeAddressI> parm,
            List<ContentDescriptorI> contentsDescriptors) {
        this.isPeer = isPeer;
        this.isFacade = isFacade;
        this.voisins = parm;
        this.contentsDescriptors = contentsDescriptors;
    }

    @Override
    public boolean isFacade() {
        return isFacade;
    }

    @Override
    public boolean isPeer() {
        return isPeer;
    }

    @Override
    public Set<NodeAddressI> getConnectedNodes() {
        return voisins;
    }

    @Override
    public List<ContentDescriptorI> getContents() {
        return this.contentsDescriptors;
    }

    @Override
    public String toString() {
        String nodeis = "";
        if (isFacade)
            nodeis += "facade";

        if (isPeer)
            nodeis += "peer";

        List<String> voisinaddr = new ArrayList<String>();
        for (NodeAddressI node : voisins) {
            try {
                voisinaddr.add(node.getNodeIdentifier());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "NodeInformation [is: " + nodeis + ", voisins=" + voisinaddr + ", content = " + contentsDescriptors
                + "]";
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

}
