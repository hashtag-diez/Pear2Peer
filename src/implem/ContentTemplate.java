package implem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    public ContentTemplate(HashMap<String, Object> toLoad) {
        this._title = (String) toLoad.get("title");
        this._albumTitle = (String) toLoad.get("album-title");
        this._composers = new HashSet<String>();
        this._interpreters = new HashSet<String>();

        ArrayList<?> composersBeforeCast = (ArrayList<?>) toLoad.get("composers");
        for (Object object : composersBeforeCast)
            this._composers.add((String) object);

        ArrayList<?> intepretersBeforeCast = (ArrayList<?>) toLoad.get("interpreters");
        if (intepretersBeforeCast != null) {
            for (Object object : intepretersBeforeCast)
                this._interpreters.add((String) object);
        }
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

    @Override
    public String toString() {
        return "ContentTemplate [\n     _title=" + _title + "\n    ,_albumTitle=" + _albumTitle
                + "\n    ,_interpreters=" + _interpreters
                + "\n    ,_composers=" + _composers + "\n   ]";
    }
}