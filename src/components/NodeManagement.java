package components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.sorbonne_u.utils.Pair;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagementPlugin;
import ports.NodeManagementInboundPort;

@OfferedInterfaces(offered = { NodeManagementCI.class })
public class NodeManagement
    extends AbstractComponent
    implements FacadeNodeAddressI {

  protected NodeManagementInboundPort NMSetterPort;

  private Set<PeerNodeAddressI> members = new HashSet<>();
  protected String uriPrefix = "NodeC";
	private String CM_PLUGIN_URI;

  protected NodeManagement(String reflectionInboundPortURI, String inboundURI, int DescriptorId) throws Exception {
    super(reflectionInboundPortURI, 8, 0);
    this.NMSetterPort = new NodeManagementInboundPort(inboundURI, this);
    this.NMSetterPort.publishPort();
    CM_PLUGIN_URI = "plug-" +reflectionInboundPortURI;
    PluginI plugin = new ContentManagementPlugin(DescriptorId) ;
		plugin.setPluginURI(CM_PLUGIN_URI);
		this.installPlugin(plugin);    
  }

  public synchronized Set<PeerNodeAddressI> addNewComers(PeerNodeAddressI a) throws Exception {
    List<PeerNodeAddressI> neighbors = new ArrayList<>(members);
    if(members.size() % 4 == 0){
      System.out.println("Nouvelle racine !");
      ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).put(a);
    }
    members.add(a);

    Set<PeerNodeAddressI> res = neighbors
    .stream()
    .skip(neighbors.size() > 0 ? neighbors.size() - 1 : 0)
    .limit(1)
    .collect(Collectors.toSet());
    return res;
  }

  public void deletePeer(PeerNodeAddressI a) throws Exception {
    if(((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).containsKey(a)){
      ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).remove(a);
    }
    members.remove(a);
  }

  @Override
  public boolean isFacade() {
    return true;
  }

  @Override
  public boolean isPeer() {
    return false;
  }

  @Override
	public fr.sorbonne_u.utils.Pair<String, String> getNodeIdentifier() throws Exception {
		return new Pair<String,String>(NMSetterPort.getPortURI(), ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).CMSetterPort.getPortURI());
	}

  @Override
  public String getNodeManagementURI() {
    return reflectionInboundPortURI;
  }
}
