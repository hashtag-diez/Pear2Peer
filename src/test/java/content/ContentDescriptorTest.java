package test.java.content;

import static org.junit.Assert.*;

import org.junit.Test;

import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentTemplateI;

public class ContentDescriptorTest {

	@Test
	public void testMatchBasic() {
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
	public void testEqualsBasic()  {
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
		
		assertTrue(
				"content 1a and 1b are equals because they have similar values in their"
				+ " respective fields (title, album, ...)", 
				contentDescriptor1a.equals(contentDescriptor1b)
			);
		assertFalse(
				"content 1a and 2 are not equals because they doesn't have similar values in their"
						+ " respective fields (title, album, ...)", 
				contentDescriptor1a.equals(contentDescriptor2)
			);
		assertFalse(
				"content 1b and 2 are not equals because they doesn't have similar values in their"
						+ " respective fields (title, album, ...)", 
				contentDescriptor1b.equals(contentDescriptor2)
			);
	}
	
	@Test
	public void testTwoContentsWithAtLeastOneDifferentFieldAreNotEquals() {
		ContentDescriptorI contentDescriptor1a = 
				new ContentDescriptorBuilder()
				.setTitle("Titre1a")
				.setAlbum("Album1")
				.addInterpreter("I1")
				.addcomposer("C1")
				.setFileSize((long) 10)
				.setAdress(null)
				.build();
		
		ContentDescriptorI contentDescriptor1b =
				new ContentDescriptorBuilder()
				.setTitle("Titre1b")
				.setAlbum("Album1")
				.addInterpreter("I1")
				.addcomposer("C1")
				.setFileSize((long) 10)
				.setAdress(null)
				.build();
		
		assertFalse(contentDescriptor1a.equals(contentDescriptor1b));
		
	}
	
	@Test
	public void testTwoContentsWithAtLeastOneSimilarFieldCanMatch() {
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
				.addcomposer("C1")
				.setFileSize((long) 10)
				.setAdress(null)
				.build();
		
		assertTrue(contentDescriptor1.match(contentDescriptor2));
	}

}
