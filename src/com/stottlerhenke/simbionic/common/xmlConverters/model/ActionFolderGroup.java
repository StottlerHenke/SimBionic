

package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActionFolderGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActionFolderGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="action" type="{}Action"/>
 *         &lt;element name="actionFolder" type="{}ActionFolder"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionFolderGroup", propOrder = {
    "actionOrActionFolder"
})
public class ActionFolderGroup {

    @XmlElements({
        @XmlElement(name = "action", type = Action.class),
        @XmlElement(name = "actionFolder", type = ActionFolder.class)
    })
    protected List<Object> actionOrActionFolder = new ArrayList<Object>();

    /**
     * Gets the value of the actionOrActionFolder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionOrActionFolder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionOrActionFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Action }
     * {@link ActionFolder }
     * 
     * 
     */
    public List<Object> getActionOrActionFolder() {
        return actionOrActionFolder;
    }
    
    public void addAction(Action action) {
       actionOrActionFolder.add(action);
    }
    
    public void removeAction(Action action) {
       actionOrActionFolder.remove(action);
    }
    
    public void addActionFolder(ActionFolder actionFolder) {
       actionOrActionFolder.add(actionFolder);
    }
    
    public void removeActionFolder(ActionFolder actionFolder) {
       actionOrActionFolder.remove(actionFolder);
    }
    
    public int size() {
    	return actionOrActionFolder.size();
    }

}
