package components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import components.interfaces.ClientCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import implem.ContentTemplate;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;
import interfaces.NodeAddressI;
import plugins.ContentManagement.ContentManagementPI;
import plugins.ContentManagement.port_connector.CMOutboundPort;
import plugins.ContentManagement.port_connector.ContentManagementServiceConnector;
import plugins.NetworkScannerStuff.NetworkScannerPI;
import plugins.NetworkScannerStuff.NodeInformationI;
import plugins.NetworkScannerStuff.port_connector.NSPoutBoundPort;
import plugins.NetworkScannerStuff.port_connector.NetworkScannerServiceConnector;
import ports.ClientInboundPort;

@OfferedInterfaces(offered = { ClientCI.class })
@RequiredInterfaces(required = { ContentManagementPI.class, NetworkScannerPI.class })
public class Client
    extends AbstractComponent {

  protected ClientInboundPort ReturnPort;
  // The port used to call the methods of the ContentManagementPI.
  protected CMOutboundPort CMGetterPort;
  // The port used to call the methods of the NetworkScannerPI.
  protected NSPoutBoundPort NSGetterPort;
  protected String CMNodeManagementInboundURI;
  protected String NSNodeManagementInboundURI;
  protected boolean found = false;

  // The constructor of the Client class. It creates the Client object and
  // initializes the ports.
  protected Client(String reflectionInboundPort, String CMNodeManagementInboundURI, String NSNodeManagementInboundURI)
      throws Exception {
    super(reflectionInboundPort, 1, 0);
    this.CMGetterPort = new CMOutboundPort(this);
    this.CMGetterPort.publishPort();
    this.CMNodeManagementInboundURI = CMNodeManagementInboundURI;

    this.NSGetterPort = new NSPoutBoundPort(this);
    this.NSGetterPort.publishPort();

    this.ReturnPort = new ClientInboundPort(this);
    this.ReturnPort.publishPort();
    this.NSNodeManagementInboundURI = NSNodeManagementInboundURI;
  }

  /**
   * It connects the two ports of the component to the two ports of the two
   * services
   */
  @Override
  public void start() throws ComponentStartException {
    try {
      super.start();
      this.doPortConnection(CMGetterPort.getPortURI(), CMNodeManagementInboundURI,
          ContentManagementServiceConnector.class.getCanonicalName());
      this.doPortConnection(NSGetterPort.getPortURI(), NSNodeManagementInboundURI,
          NetworkScannerServiceConnector.class.getCanonicalName());
    } catch (Exception e) {
      throw new ComponentStartException(e);
    }
  }

  @Override
  public void execute() throws Exception {
    super.execute();
    Thread.sleep(2000);
    mapNetwork();
    exampleSearchFind();
  }

  /**
   * It reads the templates from the data directory, picks a random one, and
   * returns it
   * 
   * @return A ContentTemplate object
   */
  public ContentTemplateI pickTemplate() throws ClassNotFoundException, IOException {
    ContentDataManager.DATA_DIR_NAME = "src/data";
    ArrayList<HashMap<String, Object>> result = ContentDataManager.readTemplates((int) Math.random() % 2);
    HashMap<String, Object> random = result.get((int) Math.random() % result.size());
    return new ContentTemplate(random);
  }

  /**
   * It picks a template, prints it, and then asks the Network for matches
   */
  public void exampleSearchContainsWichMatch() throws Exception {
    ContentTemplateI temp = pickTemplate();
    System.out.println("Template recherche :\n" + temp.toString());
    Set<ContentDescriptorI> matched = new HashSet<>();

    CMGetterPort.match(temp, matched, 5, ReturnPort.getPortURI());
  }

  /**
   * It picks a template, prints it, and then asks the Network to find it
   */
  public void exampleSearchFind() throws Exception {
    ContentTemplateI temp = pickTemplate();
    System.out.println("Template recherche :\n" + temp.toString());
    found = false;
    CMGetterPort.find(temp, 5, ReturnPort.getPortURI());
  }

  /**
   * If the result has not been found, print the result, otherwise set the result
   * to found.
   * 
   * @param matched The ContentDescriptorI object that matched the search
   *                criteria.
   */
  public void findResult(ContentDescriptorI matched) {
    if (found == false) {
      System.out.println("Found : " + matched.toString());
    } else
      found = true;
  }

  /**
   * If the set of matched content descriptors is not empty, print out the matched
   * content descriptors
   * 
   * @param matched A set of ContentDescriptorI objects that matched the query.
   */
  public void matchResult(Set<ContentDescriptorI> matched) {
    if (!matched.isEmpty()) {
      System.out.println("Matched : ");
      for (ContentDescriptorI contentDescriptor : matched) {
        System.out.println(contentDescriptor);
      }
    }
  }

  /**
   * It gets the network map from the NSGetterPort, and prints it
   */
  public void mapNetwork() throws Exception {
    HashMap<NodeAddressI, NodeInformationI> result = new HashMap<>();
    result = NSGetterPort.mapNetwork(result);
    System.out.println("Contain " + result.size() + " Nodes");
    for (Entry<NodeAddressI, NodeInformationI> nodeInfo : result.entrySet()) {
      System.out.println("Node " + nodeInfo.getKey().getNodeIdentifier() + " : ");
      System.out.println(nodeInfo.getValue());
      System.out.println("--------------------------------");
    }
  }
}
