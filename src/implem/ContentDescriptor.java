package implem;

import java.util.HashMap;
import java.util.Set;

import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import interfaces.ContentDescriptorI;
import interfaces.ContentManagementNodeAddressI;
import interfaces.ContentTemplateI;

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
    public boolean equals(ContentDescriptorI cd) throws Exception {
        /*
         * boolean addrEqual =
         * this._addr.getNodeURI().equals(cd.getContentNodeAdressI().
         * getNodeURI());
         * boolean size = this.getSize() == cd.getSize();
         */
        return matchTitles(cd) && matchAlbums(cd) && matchInterpreters(cd)
                && matchComposers(cd) /* && size && addrEqual */;
    }

    @Override
    public boolean match(ContentTemplateI request) {
    	boolean res = 
    			matchTitles(request) 
    			||  matchAlbums(request)
    			|| matchComposers(request)
    			|| matchInterpreters(request);
    	
    	return res;
    }

    protected boolean matchTitles(ContentTemplateI request) {
    	if (request.getTitle() == null) return false;
        return request.getTitle().equals(getTitle());
    }

    protected boolean matchAlbums(ContentTemplateI request) {
    	if (request.getAlbumTitle() == null) return false;
        return request.getAlbumTitle().equals(getTitle());
    }

    /**
     * > If the interpreters of the request are contained in the interpreters of the
     * content template, then
     * return true
     * 
     * @param request The request to be checked.
     * @return A boolean value.
     */
    protected boolean matchInterpreters(ContentTemplateI request) {
    	if (request.getInterpreters()== null || request.getInterpreters().isEmpty()) 
    		return false;
    	return this._interpreters.containsAll(request.getInterpreters());
    }

    /**
     * If the composers of this template contain all the composers of the request,
     * then return true.
     * 
     * @param request The request to be fulfilled.
     * @return A boolean value.
     */
    protected boolean matchComposers(ContentTemplateI request) {
    	if (request.getComposers() == null || request.getComposers().isEmpty()) 
    		return false;
    	return this._composers.containsAll(request.getComposers());
    }

    @Override
    public String toString() {
        String sup = super.toString();
        return "ContentDescriptor [\n   " + sup + "\n   _size=" + _size + "\n   , _addr=" + _addr + "\n]";
    }

}