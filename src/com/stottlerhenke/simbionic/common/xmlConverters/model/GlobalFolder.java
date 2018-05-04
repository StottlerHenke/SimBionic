package com.stottlerhenke.simbionic.common.xmlConverters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * XXX: 2018-05-03 -jmm copy-paste port of BehaviorFolder
 * for use with Global objects.
 * 
 * <p>Java class for GlobalFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalFolder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="globalChildren" type="{}GlobalFolderGroup"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalFolder", propOrder = {

})
public class GlobalFolder extends Folder {
    @XmlElement(required = true)
    protected GlobalFolderGroup globalChildren = new GlobalFolderGroup();


    /**
     * Gets the value of the globalChildren property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalFolderGroup }
     *     
     */
    public GlobalFolderGroup getGlobalChildren() {
        return globalChildren;
    }

    /**
     * Sets the value of the globalChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalFolderGroup }
     *     
     */
    public void setGlobalChildren(GlobalFolderGroup value) {
        this.globalChildren = value;
    }
}
