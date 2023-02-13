package components.interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import interfaces.ContentDescriptorI;
import interfaces.ContentTemplateI;

public interface ContentManagementCI 
extends RequiredCI, OfferedCI{
  ContentDescriptorI find(ContentTemplateI cd, int hops);
  Set<ContentDescriptorI> match(ContentTemplateI cd, 
    Set<ContentDescriptorI> matched, 
    int hops);
}
