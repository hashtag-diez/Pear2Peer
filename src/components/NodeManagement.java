package components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.sorbonne_u.utils.Pair;

import components.interfaces.ContentManagementCI;
import components.interfaces.NodeManagementCI;
import connectors.ContentManagementServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import plugins.ContentManagementPlugin;
import ports.NodeManagementInboundPort;
import ports.NodeManagementInboundPortCM;
import ports.OutboundPortCM;

@OfferedInterfaces(offered = { NodeManagementCI.class, ContentManagementCI.class })
@RequiredInterfaces(required = { ContentManagementCI.class })
public class NodeManagement
    extends AbstractComponent
    implements FacadeNodeAddressI {

  protected NodeManagementInboundPort NMSetterPort;
  protected NodeManagementInboundPortCM CMSetterPort;

  private Set<PeerNodeAddressI> members = new HashSet<>();
  protected String uriPrefix = "NodeC";
	private final String CM_PLUGIN_URI = "CM_PLUG";

  protected NodeManagement(String reflectionInboundPortURI, String inboundURI, int DescriptorId) throws Exception {
    super(reflectionInboundPortURI, 4, 0);
    this.NMSetterPort = new NodeManagementInboundPort(inboundURI, this);
    this.NMSetterPort.publishPort();
    this.CMSetterPort = new NodeManagementInboundPortCM("cm" + inboundURI, this);
    this.CMSetterPort.publishPort();
  
    PluginI plugin = new ContentManagementPlugin(DescriptorId) ;
		plugin.setPluginURI(CM_PLUGIN_URI) ;
		this.installPlugin(plugin); 
    
    this.uriPrefix = this.uriPrefix + UUID.randomUUID();
  }

  public synchronized Set<PeerNodeAddressI> addNewComers(PeerNodeAddressI a) throws Exception {
    List<PeerNodeAddressI> neighbors = new ArrayList<>(members);
    if(members.size() % 4 == 0){
      System.out.println("Nouvelle racine !");
      String oportCM = AbstractOutboundPort.generatePortURI();
      OutboundPortCM peerOutPortCM = new OutboundPortCM(oportCM, this);
      peerOutPortCM.publishPort();
      this.doPortConnection(oportCM, a.getNodeIdentifier().getSecond() ,
          ContentManagementServiceConnector.class.getCanonicalName());
      ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).put(a, peerOutPortCM);
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
      OutboundPortCM peerPortCM = ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).get(a);
      this.doPortDisconnection(peerPortCM.getPortURI());
      peerPortCM.unpublishPort();
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
		return new Pair<String,String>(NMSetterPort.getPortURI(), CMSetterPort.getPortURI());
	}

  @Override
  public String getNodeManagementURI() {
    return uriPrefix;
  }

  public ContentDescriptorI find(ContentTemplateI request, int hops) throws Exception {
    return ((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).find(request, hops);
  }

  public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
      throws Exception {
    Set<ContentDescriptorI> res =((ContentManagementPlugin)this.getPlugin(CM_PLUGIN_URI)).match(cd, matched, hops);
    return res;
  }
}
