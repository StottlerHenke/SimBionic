
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Action complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Action">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="core" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="parameters" type="List<Parameter>"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Action", propOrder = {

})
public class Action extends Function {

    protected boolean core;
    
    /**
     * Gets the value of the core property.
     * 
     */
    public boolean isCore() {
        return core;
    }

    /**
     * Sets the value of the core property.
     * 
     */
    public void setCore(boolean value) {
        this.core = value;
    }


}
