
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BehaviorFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BehaviorFolder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="behaviorChildren" type="{}BehaviorFolderGroup"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BehaviorFolder", propOrder = {

})
public class BehaviorFolder extends Folder {

    @XmlElement(required = true)
    protected BehaviorFolderGroup behaviorChildren = new BehaviorFolderGroup();

  
    /**
     * Gets the value of the behaviorChildren property.
     * 
     * @return
     *     possible object is
     *     {@link BehaviorFolderGroup }
     *     
     */
    public BehaviorFolderGroup getBehaviorChildren() {
        return behaviorChildren;
    }

    /**
     * Sets the value of the behaviorChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link BehaviorFolderGroup }
     *     
     */
    public void setBehaviorChildren(BehaviorFolderGroup value) {
        this.behaviorChildren = value;
    }

}
