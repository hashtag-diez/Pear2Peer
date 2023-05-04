package test.java.content;

import java.util.HashSet;
import java.util.Set;

import main.java.implem.ContentDescriptor;
import main.java.implem.ContentTemplate;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentManagementNodeAddressI;
import main.java.interfaces.ContentTemplateI;

/**
 * Cette classe permet de creer des contents de diverses manières. Cela nous
 * sera utile dans les phases d'écriture de tests unitaires.
 * 
 * @author ABSSI (Team)
 */
public class ContentDescriptorBuilder {

	protected String title = null;
	protected String album = null;
	protected Set<String> interpreters = new HashSet<>();
	protected Set<String> composers = new HashSet<>();
	private Long size;
	private ContentManagementNodeAddressI adresse;
	
	public ContentDescriptorBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public ContentDescriptorBuilder setAlbum(String album) {
		this.album = album;
		return this;
	}
	
	public ContentDescriptorBuilder setFileSize(Long fileSize) {
		this.size = fileSize;
		return this;
	}
	
	public ContentDescriptorBuilder setAdresse(ContentManagementNodeAddressI adress) {
		this.adresse = adress;
		return this;
	}
	
	public ContentDescriptorBuilder addInterpreter(String interpreter) {
		this.interpreters.add(interpreter);
		return this;
	}
	
	
	
	public ContentDescriptorBuilder addcomposer(String composer) {
		this.composers.add(composer);
		return this;
	}

	public ContentDescriptor build() {
		return new ContentDescriptor(title, album, interpreters, composers, size, adresse);
    }
}
