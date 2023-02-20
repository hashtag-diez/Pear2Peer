package components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.interfaces.ContentManagementCI;
import connectors.ContentManagementServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import implem.ContentTemplate;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import ports.OutboundPortCM;

@RequiredInterfaces(required = { ContentManagementCI.class })
public class Client 
  extends AbstractComponent{

  protected OutboundPortCM CMGetterPort;
  protected String CMNodeManagementInboundURI;

  protected Client(String reflectionInboundPort, String CMNodeManagementInboundURI) throws Exception {
    super(reflectionInboundPort, 1, 0);
    this.CMGetterPort = new OutboundPortCM("client", this);
    this.CMGetterPort.publishPort();
    this.CMNodeManagementInboundURI = CMNodeManagementInboundURI;
  }

  @Override
  public void start() throws ComponentStartException {
    try {
      super.start();
      System.out.println(CMNodeManagementInboundURI);
      this.doPortConnection(CMGetterPort.getPortURI(), CMNodeManagementInboundURI, ContentManagementServiceConnector.class.getCanonicalName());
    } catch (Exception e) {
      throw new ComponentStartException(e);
    }
  }

  @Override
  public void execute() throws Exception {
    super.execute();
    Thread.sleep(2000);

    ContentTemplateI temp = pickTemplate();
    System.out.println("Template recherché :\n" + temp.toString());
    ContentDescriptorI res = CMGetterPort.find(temp, 10);
    if(res==null){
      System.out.println("Pas trouvé !");
    } else{
      System.out.println("Trouvé :\n"+res.toString());
    }
  }
  
  public ContentTemplateI pickTemplate() throws ClassNotFoundException, IOException{
      ContentDataManager.DATA_DIR_NAME = "src/data";
      ArrayList<HashMap<String, Object>> result = ContentDataManager.readTemplates(1);
      HashMap<String, Object> random = result.get((int) Math.random()%result.size());
      System.out.println(random);
      return new ContentTemplate(random);
  }
}
