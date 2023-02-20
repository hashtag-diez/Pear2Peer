package components;

import java.util.ArrayList;
import java.util.HashMap;
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
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
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

  private HashMap<PeerNodeAddressI, OutboundPortCM> roots = new HashMap<>();
  private Set<PeerNodeAddressI> members = new HashSet<>();
  protected String uriPrefix = "NodeC";

  protected NodeManagement(String reflectionInboundPortURI, String inboundURI) throws Exception {
    super(reflectionInboundPortURI, 4, 0);
    this.NMSetterPort = new NodeManagementInboundPort(inboundURI, this);
    this.NMSetterPort.publishPort();
    this.CMSetterPort = new NodeManagementInboundPortCM("cm" + inboundURI, this);
    this.CMSetterPort.publishPort();
  
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
      roots.put(a, peerOutPortCM);
      System.out.println(roots.size());
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
    if(roots.containsKey(a)){
      System.out.println("Ziak");
      OutboundPortCM peerPortCM = roots.get(a);
      this.doPortDisconnection(peerPortCM.getPortURI());
      peerPortCM.unpublishPort();
      roots.remove(a);
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
  public Pair<String, String> getNodeIdentifier() throws Exception {
    return new Pair<String, String>(NMSetterPort.getPortURI(), CMSetterPort.getPortURI());
  }

  @Override
  public String getNodeManagementURI() {
    return uriPrefix;
  }

  public ContentDescriptorI find(ContentTemplateI request, int hops) throws Exception {
    System.out.println("Clicos reçu");
    ContentDescriptorI res = null;
    System.out.println(roots.size());
    for (PeerNodeAddressI node : this.roots.keySet()) {
      OutboundPortCM outBoundPort = roots.get(node);
      res = ((ContentManagementCI) outBoundPort).find(request, hops);
      if (res != null || hops==0)
        break;
    }

    return res;
  }

  public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
      throws Exception {

    for (PeerNodeAddressI node : this.roots.keySet()) {
      OutboundPortCM outBoundPort = roots.get(node);
      matched.addAll(((ContentManagementCI) outBoundPort).match(cd, matched, --hops));
    }

    return matched;
  }
}
