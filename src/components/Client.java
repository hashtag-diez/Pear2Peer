package components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import components.interfaces.ContentManagementCI;
import connectors.ContentManagementServiceConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import implem.ContentTemplate;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import ports.OutboundPortCM;

@RequiredInterfaces(required = { ContentManagementCI.class })
public class Client
    extends AbstractComponent {

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

      ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
      rop.publishPort();

      this.doPortConnection(
          rop.getPortURI(),
          CMNodeManagementInboundURI,
          ReflectionConnector.class.getCanonicalName());

      String[] otherInboundPortUI = rop.findInboundPortURIsFromInterface(ContentManagementCI.class);
      if (otherInboundPortUI.length == 0 || otherInboundPortUI == null) {
        System.out.println("NOPE");
      } else {
        this.doPortConnection(CMGetterPort.getPortURI(), otherInboundPortUI[0],
            ContentManagementServiceConnector.class.getCanonicalName());
      }
      this.doPortDisconnection(rop.getPortURI());
      rop.unpublishPort();
      rop.destroyPort();

    } catch (Exception e) {
      throw new ComponentStartException(e);
    }
  }

  @Override
  public void execute() throws Exception {
    super.execute();
    Thread.sleep(2000);
    exampleSearchContainsWichMatch();

    /* ContentTemplateI temp = pickTemplate();
    System.out.println("Template recherché :\n" + temp.toString());
    ContentDescriptorI res = CMGetterPort.find(temp, 10);
    if (res == null) {
      System.out.println("Pas trouvé !");
    } else {
      System.out.println("Trouvé :\n" + res.toString());
    } */

  }

  public ContentTemplateI pickTemplate() throws ClassNotFoundException, IOException {
    ContentDataManager.DATA_DIR_NAME = "src/data";
    ArrayList<HashMap<String, Object>> result = ContentDataManager.readTemplates((int) Math.random() % 2);
    HashMap<String, Object> random = result.get((int) Math.random() % result.size());
    return new ContentTemplate(random);
  }

  public void exampleSearchContainsWichMatch() throws Exception {
    ContentTemplateI temp = pickTemplate();
    System.out.println("Template recherche :\n" + temp.toString());
    Set<ContentDescriptorI> matched = new HashSet<>();

    matched = CMGetterPort.match(temp, matched, 3);

    if (matched.isEmpty()) {
      System.out.println("Any matched element found");
    } else {
      for (ContentDescriptorI contentDescriptor : matched) {
        System.out.println(contentDescriptor);
      }
    }
  }
}
