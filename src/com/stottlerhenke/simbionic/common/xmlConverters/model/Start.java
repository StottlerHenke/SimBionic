
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Start complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Start">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="connectors" type="{}List<Connector>"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Start", propOrder = {

})
public class Start implements Serializable {

    protected int id;
    protected int type;
    @XmlElement(required = true)
    protected List<Connector> connectors = new ArrayList<Connector>();

    
    public Start() {
    }
    
    /**
    * Copy constructor.
    * @param s the Start to copy.
    */
   public Start(Start s) {
       id = s.getId();
       type = s.getType();
       
       for (Connector c : s.getConnectors()) {
	  connectors.add(new Connector(c));
       }
    }
    
    
    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the type property.
     * 
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     */
    public void setType(int value) {
        this.type = value;
    }

    /**
     * Gets the value of the connectors property.
     * 
     * @return
     *     possible object is
     *     {@link List<Connector> }
     *     
     */
    public List<Connector> getConnectors() {
        return connectors;
    }

    /**
     * Sets the value of the connectors property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Connector> }
     *     
     */
    public void setConnectors(List<Connector> value) {
        this.connectors = value;
    }
    
    public void addConnector(Connector connector) {
       connectors.add(connector);
    }
    
    public void removeConnector(Connector connector) {
       connectors.remove(connector);
    }

}
