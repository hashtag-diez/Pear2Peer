package main.java.implem;

import java.util.HashMap;
import java.util.Set;

import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import main.java.interfaces.ContentDescriptorI;
import main.java.interfaces.ContentManagementNodeAddressI;
import main.java.interfaces.ContentTemplateI;

public class ContentDescriptor extends ContentTemplate implements ContentDescriptorI {

    /*
     * TODO
     * Ne pas transmettre la référence à l'objet ContentNodeAddressI directement,
     * mais plutôt l'adresse en elle-même
     */
    public ContentDescriptor(String title, String albumTitle, Set<String> interpreters, Set<String> composers,
            Long fileSize, ContentManagementNodeAddressI addr) {
        super(title, albumTitle, interpreters, composers);
        this._size = fileSize;
        this._addr = addr;

    }

    public ContentDescriptor(HashMap<String, Object> toLoad, ContentManagementNodeAddressI addr) {
        super(toLoad);
        this._size = (Long) toLoad.get(ContentDataManager.SIZE_KEY);
        this._addr = addr;
    }

    protected Long _size = Long.valueOf(0);
    protected ContentManagementNodeAddressI _addr;

    /**
     * > This function returns the address of the content node
     * 
     * @return The address of the content node.
     */
    @Override
    public ContentManagementNodeAddressI getContentNodeAdressI() {
        return this._addr;
    }

    @Override
    public long getSize() {
        return this._size; // Taille du MP3
    }

    @Override
    public boolean equals(ContentDescriptorI cd) {
        /*
         * boolean addrEqual =
         * this._addr.getNodeURI().equals(cd.getContentNodeAdressI().
         * getNodeURI());
         * boolean size = this.getSize() == cd.getSize();
         */
        return matchTitles(cd) && matchAlbums(cd) && matchComposers(cd)
                && matchInterpreters(cd) ;
    }

    @Override
    public boolean match(ContentTemplateI request) {
    	boolean res = 
    			matchTitles(request) 
    			|| matchAlbums(request)
    			|| matchComposers(request)
    			|| matchInterpreters(request);

    	return res;
    }

    protected boolean matchTitles(ContentTemplateI request) {
        if(getTitle()==null || request.getTitle()==null) return false;
        return request.getTitle().equals(getTitle());
    }

    protected boolean matchAlbums(ContentTemplateI request) {
        if(getAlbumTitle()==null || request.getAlbumTitle()==null) return false;
        return request.getAlbumTitle().equals(getAlbumTitle());
    }

    /**
     * If the two interpreters have at least one common interpreter,
     * they match
     * @param request The request to be checked.
     * @return A boolean value.
     */
    protected boolean matchInterpreters(ContentTemplateI request) {
        if(request.getInterpreters().size()==0 || _interpreters.size()==0) return false;
    	return this._interpreters
    			.stream().anyMatch(ele -> request.getInterpreters().contains(ele));
    }

    /**
     * If the two composers have at least one common composer
     * they match
     * @param request The request to be fulfilled.
     * @return A boolean value.
     */
    protected boolean matchComposers(ContentTemplateI request) {
        if(request.getComposers().size()==0 || _composers.size()==0) return false;
    	return this._composers
    			.stream().anyMatch(ele -> request.getComposers().contains(ele));
    }

    @Override
    public String toString() {
        String sup = super.toString();
        return "ContentDescriptor [\n   " + sup + "\n   _size=" + _size + "\n   , _addr=" + _addr + "\n]";
    }

}