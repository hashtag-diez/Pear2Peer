package test.java.content;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import main.java.implem.ContentDescriptor;
import main.java.implem.ContentTemplate;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentTemplateI;

public class ContentDescriptorTest {

	protected ContentTemplateI buildContentTemplateById(int id) {
		// content fields
		String titre = "titre" + id;
		String album = "album" + id;
		Set<String> interpreters = new HashSet<>();
		interpreters.add("interpreter" + id);
		Set<String> composers = new HashSet<>();
		composers.add("composer" + id);

		return new ContentTemplate(titre, album, interpreters, composers);
	}
	
	protected ContentDescriptorI buildContenDescriptorById(int id) {
		// content fields
		String titre = "titre" + id;
		String album = "album" + id;
		Set<String> interpreters = new HashSet<>();
		interpreters.add("interpreter" + id);
		Set<String> composers = new HashSet<>();
		composers.add("composer" + id);
		return new ContentDescriptor(titre, album, interpreters, composers, (long) id, null);
	}


	@Test
	public void testMatch() {
		ContentTemplateI template1 = buildContentTemplateById(1);
		ContentTemplateI template2  = buildContentTemplateById(2);
		ContentDescriptorI contentDescriptor1 = buildContenDescriptorById(1);
		ContentDescriptorI contentDescriptor2 = buildContenDescriptorById(2);
		
		assertTrue(contentDescriptor1.match(template1));
		assertFalse(contentDescriptor1.match(template2));
		assertTrue(contentDescriptor2.match(template2));
		assertFalse(contentDescriptor2.match(template1));
	}

}
