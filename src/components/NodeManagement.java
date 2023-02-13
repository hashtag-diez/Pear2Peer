package components;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import components.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import interfaces.FacadeNodeAddressI;
import interfaces.PeerNodeAddressI;
import ports.NodeManagementInboundPort;

@OfferedInterfaces(offered = {NodeManagementCI.class})
public class NodeManagement 
extends AbstractComponent
implements FacadeNodeAddressI{

  protected NodeManagementInboundPort NMSetterPort;
  private Set<PeerNodeAddressI> members = new HashSet<>();
  protected String uriPrefix = "NodeC";

  protected NodeManagement(String reflectionInboundPortURI, String inboundURI) throws Exception{
    super(reflectionInboundPortURI, 1, 0);
    this.NMSetterPort = new NodeManagementInboundPort(inboundURI, this);
    this.NMSetterPort.publishPort();
    this.uriPrefix = this.uriPrefix + UUID.randomUUID();
  }

  public Set<PeerNodeAddressI> addNewComers(PeerNodeAddressI a) throws Exception{
    Set<PeerNodeAddressI> neighbors = new HashSet<>(members);
    members.add(a);
    return neighbors;
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
  public String getNodeIdentifier() throws Exception {
    return NMSetterPort.getPortURI();
  }

  @Override
  public String getNodeManagementURI() {
    return uriPrefix;
  }
}
