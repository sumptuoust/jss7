package org.mobicents.ss7.linkset.oam;

import javolution.util.FastMap;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * Instance of this class represents the logical group of links between two SP
 * 
 * @author amit bhayani
 */
public abstract class Linkset implements XMLSerializable {

    private static final String LINKSET_NAME = "name";
    private static final String LINKSET_STATE = "state";
    private static final String LINKSET_MODE = "mode";
    private static final String LINKSET_OPC = "opc";
    private static final String LINKSET_DPC = "dpc";
    private static final String LINKSET_NI = "ni";
    private static final String LINKS = "links";
    private static final String LINK = "link";

    protected static final boolean TRUE = true;
    protected static final boolean FALSE = false;

    protected String linksetName = null;
    protected int apc;
    protected int opc;
    protected int ni = 2;

    protected int state = LinksetState.UNAVAILABLE;
    protected int mode = LinksetMode.UNCONFIGURED;

    protected LinksetSelectorKey selectorKey = null;

    protected LinksetStream linksetStream = null;

    // Hold Links here. Link name as key and actual Link as Object
    protected FastMap<String, Link> links = new FastMap<String, Link>();

    public Linkset() {
        this.initialize();
    }

    public Linkset(String linksetName, int opc, int apc, int ni) {
        this();
        this.linksetName = linksetName;
        this.opc = opc;
        this.apc = apc;
        this.ni = ni;
    }

    /**
     * Initialize this linkset after creating a new instance
     */
    protected abstract void initialize();

    /**
     * Configure this linkset
     * 
     * @throws Exception
     */
    protected abstract void configure() throws Exception;

    /**
     * Get handle to underlying stream
     * 
     * @return
     */
    public LinksetStream getLinksetStream() {
        return this.linksetStream;
    }

    /**
     * Get adjacent point code
     * 
     * @return
     */
    public int getApc() {
        return apc;
    }

    /**
     * Set adjacent point code
     * 
     * @param dpc
     */
    public void setApc(int dpc) {
        this.apc = dpc;
    }

    /**
     * Get originating point code
     * 
     * @return
     */
    public int getOpc() {
        return opc;
    }

    /**
     * Set originating point code
     * 
     * @param opc
     */
    public void setOpc(int opc) {
        this.opc = opc;
    }

    /**
     * Get network-indicator
     * 
     * @return
     */
    public int getNi() {
        return ni;
    }

    /**
     * Set network-indicator
     * 
     * @param ni
     */
    public void setNi(int ni) {
        this.ni = ni;
    }

    /**
     * Get linkset name
     * 
     * @return
     */
    public String getName() {
        return linksetName;
    }

    /**
     * Get linkset state
     * 
     * @return
     */
    public int getState() {
        return state;
    }

    /**
     * Set linkset state
     * 
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Get linkset Mode
     * 
     * @return
     */
    public int getMode() {
        return mode;
    }

    /**
     * Get the map of link name vs {@link Link}
     * 
     * @return
     */
    public FastMap<String, Link> getLinks() {
        return links;
    }

    /**
     * Set links
     * 
     * @param links
     */
    public void setLinks(FastMap<String, Link> links) {
        this.links = links;
    }

    /**
     * Get the link corresponding to passed linkName. If no link corresponding
     * to this linkname is present null is returned
     * 
     * @param linkName
     * @return
     */
    public Link getLink(String linkName) {
        return this.links.get(linkName);
    }

    /**
     * Operations
     */

    /**
     * Create new {@link Link}
     */
    public abstract void createLink(String[] options) throws Exception;

    /**
     * Delete existing {@link Link}
     * 
     * @param linkName
     * @throws Exception
     */
    public abstract void deleteLink(String linkName) throws Exception;

    /**
     * Activate this linkset
     * 
     * @throws Exception
     */
    public abstract void activate() throws Exception;

    /**
     * Deactivate this linkset
     * 
     * @throws Exception
     */
    public abstract void deactivate() throws Exception;

    /**
     * Activate link
     * 
     * @param linkName
     * @throws Exception
     */
    public abstract void activateLink(String linkName) throws Exception;

    /**
     * deactivate link
     * 
     * @param linkName
     * @throws Exception
     */
    public abstract void deactivateLink(String linkName) throws Exception;

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<Linkset> LINKSET_XML = new XMLFormat<Linkset>(
            Linkset.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml,
                Linkset linkSet) throws XMLStreamException {
            linkSet.linksetName = xml.getAttribute(LINKSET_NAME).toString();
            linkSet.state = xml.getAttribute(LINKSET_STATE,
                    LinksetState.UNAVAILABLE);
            linkSet.mode = xml.getAttribute(LINKSET_MODE,
                    LinksetMode.UNCONFIGURED);
            linkSet.opc = xml.getAttribute(LINKSET_OPC, -1);
            linkSet.apc = xml.getAttribute(LINKSET_DPC, -1);
            linkSet.ni = xml.getAttribute(LINKSET_NI, 2);
            int linksCount = xml.getAttribute(LINKS, 0);

            for (int i = 0; i < linksCount; i++) {
                Link link = xml.get(LINK);
                link.setLinkSet(linkSet);
                linkSet.links.put(link.getName(), link);
            }
        }

        @Override
        public void write(Linkset linkSet,
                javolution.xml.XMLFormat.OutputElement xml)
                throws XMLStreamException {
            xml.setAttribute(LINKSET_NAME, linkSet.linksetName);
            xml.setAttribute(LINKSET_STATE, linkSet.state);
            xml.setAttribute(LINKSET_MODE, linkSet.mode);
            xml.setAttribute(LINKSET_OPC, linkSet.opc);
            xml.setAttribute(LINKSET_DPC, linkSet.apc);
            xml.setAttribute(LINKSET_NI, linkSet.ni);
            xml.setAttribute(LINKS, linkSet.links.size());

            for (FastMap.Entry<String, Link> e = linkSet.getLinks().head(), end = linkSet
                    .getLinks().tail(); (e = e.getNext()) != end;) {
                Link value = e.getValue();
                xml.add(value, LINK, value.getClass().getName());
            }

        }
    };
}
