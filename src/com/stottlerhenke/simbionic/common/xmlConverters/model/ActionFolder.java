


package com.stottlerhenke.simbionic.common.xmlConverters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActionFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActionFolder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="actionChildren" type="{}ActionFolderGroup"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionFolder", propOrder = {

})
public class ActionFolder extends Folder {

    @XmlElement(required = true)
    protected ActionFolderGroup actionChildren = new ActionFolderGroup();


    /**
     * Gets the value of the actionChildren property.
     * 
     * @return
     *     possible object is
     *     {@link ActionFolderGroup }
     *     
     */
    public ActionFolderGroup getActionChildren() {
        return actionChildren;
    }

    /**
     * Sets the value of the actionChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionFolderGroup }
     *     
     */
    public void setActionChildren(ActionFolderGroup value) {
        this.actionChildren = value;
    }

}
