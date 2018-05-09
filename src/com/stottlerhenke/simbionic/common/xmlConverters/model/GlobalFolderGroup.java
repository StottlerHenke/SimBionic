package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * XXX: 2018-05-03 -jmm copy-paste port of BehaviorFolderGroup
 * for use with Global objects.
 * 
 * <p>Java class for GlobalFolderGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalFolderGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="global" type="{}Global"/>
 *         &lt;element name="globalFolder" type="{}GlobalFolder"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalFolderGroup", propOrder = {
    "globalOrGlobalFolder"
})
public class GlobalFolderGroup {

    /**
     * XXX: 2018-05-03 -jmm <br>
     * As far as I can tell, the only reason why this list is
     * {@code List<Object>} is because Java does not support union types in
     * generics; a separate class might not be strictly necessary, but is
     * provided to communicate {@code globalOrGlobalFolder} should make sure
     * that elements should be one of the two types (Contrast with the approach
     * before folders, where a "bare" {@code List<Global>} was used by the
     * {@code SimBionicJava} model instead of an instance of a custom class
     * like GlobalFolderGroup.)
     */
    @XmlElements({
            @XmlElement(name = "globalFolder", type = GlobalFolder.class),
            @XmlElement(name = "global", type = Global.class) })
    protected List<Object> globalOrGlobalFolder = new ArrayList<Object>();

    /**
     * Gets the value of the globalOrGlobalFolder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the globalOrGlobalFolder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getGlobalOrGlobalFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalFolder } {@link Global }
     * 
     * 
     */
    public List<Object> getGlobalOrGlobalFolder() {
        return globalOrGlobalFolder;
    }

    public void addGlobal(Global global) {
        globalOrGlobalFolder.add(global);
    }

    public void removeGlobal(Global global) {
        globalOrGlobalFolder.remove(global);
    }

    public void addGlobalFolder(GlobalFolder globalFolder) {
        globalOrGlobalFolder.add(globalFolder);
    }

    public void removeGlobalFolder(GlobalFolder globalFolder) {
        globalOrGlobalFolder.remove(globalFolder);
    }

}
