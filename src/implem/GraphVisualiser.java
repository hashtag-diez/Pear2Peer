package implem;

import java.util.HashMap;
import java.util.Map.Entry;

import interfaces.NodeAddressI;
import plugins.NetworkScanner.NodeInformationI;

public class GraphVisualiser {
    protected int quality_dpi = 40;
    protected String frontname = "Helvetica,Arial,sans-serif";
    HashMap<NodeAddressI, String> rename;
    int counter = 0;

    public String makeGraph(HashMap<NodeAddressI, NodeInformationI> mapInfo) {
        rename = new HashMap<NodeAddressI, String>();
        counter = 0;
        StringBuilder bld = new StringBuilder();
        for (Entry<NodeAddressI, NodeInformationI> entry : mapInfo.entrySet()) {
            try {
                bld.append(translateNode(entry));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return String.format("digraph {\n%s\n%s}\n", makeHeader(), bld.toString());
    }

    public int getQuality_dpi() {
        return quality_dpi;
    }

    public void setQuality_dpi(int quality_dpi) {
        this.quality_dpi = quality_dpi;
    }

    public String getFrontname() {
        return frontname;
    }

    public void setFrontname(String frontname) {
        this.frontname = frontname;
    }

    protected String makeHeader() {
        return String
                .format("graph [ dpi = %d ];\n fontname=\"%s\";\nnode [fontname=\"%s\"];\nedge [fontname=\"%s\"];",
                        quality_dpi, frontname, frontname, frontname);
    }

    protected String getNodeId(NodeAddressI n) {
        String nodeId = rename.get(n);
        if (nodeId != null)
            return nodeId;

        if (n.isFacade()) {
            nodeId = String.format("Facade%d", counter++);
        } else if (n.isPeer())
            nodeId = String.format("Peer%d", counter++);

        rename.put(n, nodeId);
        return nodeId;
    }

    protected String nodeDetails(NodeAddressI current, NodeInformationI info) {
        if (current.isPeer() && info.isRoot())
            return String.format("label=\"R\" color=green");
        if (current.isFacade())
            return String.format("label=\"\" color=red");
        if (current.isPeer())
            return String.format("label=\"\" color=black");
        return "";
    }

    protected String translateNode(Entry<NodeAddressI, NodeInformationI> node) throws Exception {
        StringBuilder bld = new StringBuilder();
        NodeAddressI current = node.getKey();
        NodeInformationI info = node.getValue();
        for (NodeAddressI voisin : info.getConnectedNodes()) {
            if (current.isFacade())
                bld.append(String.format("  %s -> %s [color=red] \n", getNodeId(current), getNodeId(voisin)));
            else
                bld.append(String.format("  %s -> %s \n", getNodeId(current), getNodeId(voisin)));
        }
        bld.append(String.format("  %s [%s] \n", getNodeId(current), nodeDetails(current, info)));
        return bld.toString();
    }

}
