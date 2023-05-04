package test.java.content;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import main.java.implem.ContentDescriptor;
import main.java.implem.ContentTemplate;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentTemplateI;

public class ContentDescriptorTest {

	@Test
	public void testMatch() {
		ContentTemplateI contentTemplate1 = 
				new ContentTemplateBuilder()
				.setTitle("Titre1")
				.setAlbum("Album1")
				.addInterpreter("I1")
				.addcomposer("C1")
				.build();
		
		ContentTemplateI contentTemplate2 = 
				new ContentTemplateBuilder()
				.setTitle("Titre2")
				.setAlbum("Album2")
				.addInterpreter("I2")
				.addcomposer("C2")
				.build();
		
		ContentDescriptorI contentDescriptor1 = 
				new ContentDescriptorBuilder()
				.setTitle("Titre1")
				.setAlbum("Album1")
				.addInterpreter("I1")
				.addcomposer("C1")
				.setFileSize((long) 10)
				.setAdress(null)
				.build();
		
		ContentDescriptorI contentDescriptor2 = 
				new ContentDescriptorBuilder()
				.setTitle("Titre2")
				.setAlbum("Album2")
				.addInterpreter("I2")
				.addcomposer("C2")
				.setFileSize((long) 10)
				.setAdress(null)
				.build();
		
		assertTrue(contentDescriptor1.match(contentTemplate1));
		assertFalse(contentDescriptor1.match(contentTemplate2));
		assertTrue(contentDescriptor2.match(contentTemplate2));
		assertFalse(contentDescriptor2.match(contentTemplate1));
		 
	}
	
	@Test
	public void testEquals()  {
		ContentDescriptorI contentDescriptor1a = 
				new ContentDescriptorBuilder()
				.setTitle("Titre1")
				.setAlbum("Album1")
				.addInterpreter("I1")
				.addcomposer("C1")
				.setFileSize((long) 10)
				.setAdress(null)
				.build();
		ContentDescriptorI contentDescriptor1b =
				new ContentDescriptorBuilder()
				.setTitle("Titre1")
				.setAlbum("Album1")
				.addInterpreter("I1")
				.addcomposer("C1")
				.setFileSize((long) 10)
				.setAdress(null)
				.build();
		ContentDescriptorI contentDescriptor2 = 
				new ContentDescriptorBuilder()
				.setTitle("Titre2")
				.setAlbum("Album2")
				.addInterpreter("I2")
				.addcomposer("C2")
				.setFileSize((long) 10)
				.setAdress(null)
				.build();
		
		assertTrue(contentDescriptor1a.equals(contentDescriptor1b));
		assertFalse(contentDescriptor1a.equals(contentDescriptor2));
		assertFalse(contentDescriptor1b.equals(contentDescriptor2));
	}

}
