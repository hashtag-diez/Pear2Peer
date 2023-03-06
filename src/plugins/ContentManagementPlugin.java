package plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import components.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import implem.ContentDescriptor;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.PeerNodeAddressI;
import ports.NodeInboundPortCM;
import ports.OutboundPortCM;

public class ContentManagementPlugin 
  extends AbstractPlugin {
  
    public NodeInboundPortCM				CMSetterPort;
		public final Class<? extends OfferedCI>	offeredInterface;
    protected Map<PeerNodeAddressI, OutboundPortCM> peersGetterPorts;
    protected List<ContentDescriptorI> contentsDescriptors;

    public ContentManagementPlugin(
      int DescriptorId
			) throws Exception{
        super();
        this.offeredInterface = ContentManagementCI.class;
        contentsDescriptors = new ArrayList<>();
        this.loadDescriptors(6 + DescriptorId);
      }
    @Override
    public void initialise() throws Exception {
      this.peersGetterPorts = new HashMap<>();
      this.CMSetterPort = new NodeInboundPortCM(this.getPluginURI(), this.getOwner());
        this.CMSetterPort.publishPort();
    }
    @Override
    public void			installOn(ComponentI owner) throws Exception
    {
      super.installOn(owner);
      this.addOfferedInterface(ContentManagementCI.class);
    }

    public void put(PeerNodeAddressI node, OutboundPortCM peerOutPortCM){
      this.peersGetterPorts.put(node, peerOutPortCM);
    }
    public OutboundPortCM get(PeerNodeAddressI node){
      OutboundPortCM outBoundPortCM = this.peersGetterPorts.get(node);
      return outBoundPortCM;
    }
    public void remove(PeerNodeAddressI node){
      this.peersGetterPorts.remove(node);
    }
    public void loadDescriptors(int number) throws Exception {
      ContentDataManager.DATA_DIR_NAME = "src/data";
      ArrayList<HashMap<String, Object>> result = ContentDataManager.readDescriptors(number);
      for (HashMap<String, Object> obj : result) {
        ContentDescriptorI readDescriptor = new ContentDescriptor(obj);
        contentsDescriptors.add(readDescriptor);
      }
    }
    public ContentDescriptorI find(ContentTemplateI request, int hops) throws Exception {
      for (ContentDescriptorI localCd : this.contentsDescriptors) {
        if (localCd.match(request)){
          return localCd;
        }	
      }
      if (hops-- == 0)
        return null;
  
      for (PeerNodeAddressI node : this.peersGetterPorts.keySet()) {
        OutboundPortCM outBoundPort = peersGetterPorts.get(node);
        if (outBoundPort != null) {
          ContentDescriptorI res = ((ContentManagementCI) outBoundPort).find(request, hops);
          if (res != null)
            return res;
        }
      }
  
      return null;
    }

    public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
        throws Exception {
      for (ContentDescriptorI localCd : this.contentsDescriptors) {
        if (localCd.match(cd)) {
          matched.add(localCd);
        }
      }
  
      if (hops != 0) {
        for (PeerNodeAddressI node : this.peersGetterPorts.keySet()) {
          OutboundPortCM outBoundPort = peersGetterPorts.get(node)  ;
          if (outBoundPort != null) {
            matched.addAll(((ContentManagementCI) outBoundPort).match(cd, matched, --hops));
          }
        }
      }
      return matched;
    }
    public boolean containsKey(PeerNodeAddressI a) {
      return this.peersGetterPorts.containsKey(a);
    }
}
