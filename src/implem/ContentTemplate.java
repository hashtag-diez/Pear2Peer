package implem;

import java.util.Set;

import interfaces.ContentTemplateI;

/**
 * ContentDescriptor
 */
public class ContentTemplate implements ContentTemplateI {
    String _title, _albumTitle;
    Set<String> _interpreters, _composers;

    public ContentTemplate() {

    }

    public ContentTemplate(String title, String albumTitle, Set<String> interpreters, Set<String> composers) {
        this._title = title;
        this._albumTitle = albumTitle;
        this._interpreters = interpreters;
        this._composers = composers;
    }

    @Override
    public String getTitle() {
        return this._title;
    }

    @Override
    public String getAlbumTitle() {
        return this._albumTitle;
    }

    @Override
    public Set<String> getInterpreters() {
        return this._interpreters;
    }

    @Override
    public Set<String> getComposers() {
        return this._composers;
    }
}