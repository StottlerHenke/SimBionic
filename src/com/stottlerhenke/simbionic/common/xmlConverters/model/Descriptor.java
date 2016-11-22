
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Descriptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Descriptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descrptorChildren" type="{}List<Descriptor>"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Descriptor", propOrder = {

})
public class Descriptor {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected List<Descriptor> descriptors = new ArrayList<Descriptor>();
    
    protected boolean _selected = false;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the descriptors property.
     * 
     * @return
     *     possible object is
     *     {@link List<Descriptor> }
     *     
     */
    public List<Descriptor> getDescriptors() {
        return descriptors;
    }

    /**
     * Sets the value of the descriptors property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Descriptor> }
     *     
     */
    public void setDescriptors(List<Descriptor> value) {
        this.descriptors = value;
    }
    
    public boolean isSelected() {
       return _selected;
    }
    
    public void setSelected(boolean selected) {
       _selected = selected;
    }

    public void addDescriptor(Descriptor d) {
       descriptors.add(d);
    }
    
    public void removeDescriptor(Descriptor d) {
       descriptors.remove(d);
    }
}
