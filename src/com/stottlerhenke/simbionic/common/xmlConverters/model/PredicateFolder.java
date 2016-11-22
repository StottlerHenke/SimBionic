
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PredicateFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PredicateFolder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="predicateChildren" type="{}PredicateFolderGroup"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PredicateFolder", propOrder = {

})
public class PredicateFolder extends Folder {

    @XmlElement(required = true)
    protected PredicateFolderGroup predicateChildren = new PredicateFolderGroup();


    /**
     * Gets the value of the predicateChildren property.
     * 
     * @return
     *     possible object is
     *     {@link PredicateFolderGroup }
     *     
     */
    public PredicateFolderGroup getPredicateChildren() {
        return predicateChildren;
    }

    /**
     * Sets the value of the predicateChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link PredicateFolderGroup }
     *     
     */
    public void setPredicateChildren(PredicateFolderGroup value) {
        this.predicateChildren = value;
    }

}
