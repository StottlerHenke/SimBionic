package com.stottlerhenke.simbionic.common.xmlConverters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * XXX: 2018-05-03 -jmm copy-paste port of BehaviorFolder
 * for use with Constant objects.
 * 
 * <p>Java class for ConstantFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConstantFolder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="constantChildren" type="{}ConstantFolderGroup"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConstantFolder", propOrder = {

})
public class ConstantFolder extends Folder {

    @XmlElement(required = true)
    protected ConstantFolderGroup constantChildren = new ConstantFolderGroup();

    /**
     * Gets the value of the constantChildren property.
     * 
     * @return
     *     possible object is
     *     {@link ConstantFolderGroup }
     *     
     */
    public ConstantFolderGroup getConstantChildren() {
        return constantChildren;
    }

    /**
     * Sets the value of the constantChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstantFolderGroup }
     *     
     */
    public void setConstantChildren(ConstantFolderGroup value) {
        this.constantChildren = value;
    }
}
