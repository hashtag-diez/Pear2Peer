package implem;

import java.util.HashMap;
import java.util.Set;

import interfaces.ContentDescriptorI;
import interfaces.ContentNodeAddressI;
import interfaces.ContentTemplateI;

public class ContentDescriptor extends ContentTemplate implements ContentDescriptorI {

    /* TODO
     * Ne pas transmettre la référence à l'objet ContentNodeAddressI directement, 
     * mais plutôt l'adresse en elle-même
     */
    public ContentDescriptor(String title, String albumTitle, Set<String> interpreters, Set<String> composers,
            Long fileSize, ContentNodeAddressI addr) {
        super(title, albumTitle, interpreters, composers);
        this._size = fileSize;
        this._addr = addr;

    }

    public ContentDescriptor(HashMap<String, Object> toLoad, ContentNodeAddressI addr) {
        super(toLoad);
        this._size = (Long) toLoad.get("size");
        this._addr = addr;
    }

    protected Long _size = Long.valueOf(0);
    protected ContentNodeAddressI _addr;

    /**
     * > This function returns the address of the content node
     * 
     * @return The address of the content node.
     */
    @Override
    public ContentNodeAddressI getContentNodeAdressI() {
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
        return _isTitleEquals(cd) && _isAlbumTitleEquals(cd) && _isIntrepretersContains(cd)
                && _isComposersContains(cd) /* && size && addrEqual */;
    }

    @Override
    public boolean match(ContentTemplateI request) {
        boolean res = false;
        if (request.getTitle() != null)
            res = _isTitleEquals(request);
        if (request.getAlbumTitle() != null)
            res = _isAlbumTitleEquals(request);
        if (request.getComposers().size() != 0)
            res = _isComposersContains(request);
        if (request.getInterpreters().size() != 0)
            res = _isIntrepretersContains(request);
        return res;
    }

    private boolean _isTitleEquals(ContentTemplateI request) {
        return request.getTitle().equals(getTitle());
    }

    private boolean _isAlbumTitleEquals(ContentTemplateI request) {
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
    private boolean _isIntrepretersContains(ContentTemplateI request) {
        return getInterpreters().containsAll(request.getInterpreters());
    }

    /**
     * If the composers of this template contain all the composers of the request,
     * then return true.
     * 
     * @param request The request to be fulfilled.
     * @return A boolean value.
     */
    private boolean _isComposersContains(ContentTemplateI request) {
        // System.out.println(getComposers().containsAll(request.getComposers()));
        return getComposers().containsAll(request.getComposers());
    }

    @Override
    public String toString() {
        String sup = super.toString();
        return "ContentDescriptor [\n   " + sup + "\n   _size=" + _size + "\n   , _addr=" + _addr + "\n]";
    }

}