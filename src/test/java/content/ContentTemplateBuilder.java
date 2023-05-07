package test.java.content;

import java.util.HashSet;
import java.util.Set;

import main.java.implem.ContentTemplate;

/**
 * Cette classe permet de creer des contents de diverses manières. Cela nous
 * sera utile dans les phases d'écriture de tests unitaires.
 * 
 * @author ABSSI (Team)
 */
public class ContentTemplateBuilder {

	protected String title = null;
	protected String album = null;
	protected Set<String> interpreters = new HashSet<>();
	protected Set<String> composers = new HashSet<>();

	public ContentTemplateBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public ContentTemplateBuilder setAlbum(String album) {
		this.album = album;
		return this;
	}

	public ContentTemplateBuilder addInterpreter(String interpreter) {
		this.interpreters.add(interpreter);
		return this;
	}

	public ContentTemplateBuilder addcomposer(String composer) {
		this.composers.add(composer);
		return this;
	}

	public ContentTemplate build() {
		return new ContentTemplate(title, album, interpreters, composers);
	}
}
