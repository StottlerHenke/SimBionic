

package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActionNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActionNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="expr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cx" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cy" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="labelMode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="isFinal" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isBehavior" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="bindings" type="{}List<Binding>"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionNode", propOrder = {

})
public class ActionNode extends Node {

    @XmlElement(required = true)
    protected String expr = "";
    @XmlElement(name = "final")
    protected boolean _final = false;
    @XmlElement(name = "isBehavior")
    protected boolean isBehavior = false;
    
    protected boolean _isAlways = false;
    
    protected boolean _isCatch = false;

    
    public ActionNode() {
    }
    
    /**
    * Copy constructor.
    * @param n the ActionNode to copy.
    */
   public ActionNode(ActionNode n) {
       super(n);
       expr = n.getExpr();
       _final = n.isFinal();
       isBehavior = n.isBehavior();
       _isAlways = n.isAlways();
       _isCatch = n.isCatch();
    }
    
    
    /**
     * Gets the value of the expr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpr() {
        return expr;
    }

    /**
     * Sets the value of the expr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpr(String value) {
        this.expr = value;
    }


    /**
     * Gets the value of the final property.
     * 
     */
    public boolean isFinal() {
        return _final;
    }

    /**
     * Sets the value of the final property.
     * 
     */
    public void setIsFinal(boolean value) {
        this._final = value;
    }
    
    public boolean isBehavior() {
       return isBehavior;
    }
    
    public void setIsBehavior(boolean value) {
       isBehavior = value;
    }
    
    public void setAlways(boolean isAlways) {
    	_isAlways = isAlways;
    }
    
    public boolean isAlways() {
    	return _isAlways;
    }
    
    public void setCatch(boolean isCatch) {
    	_isCatch = isCatch;
    }
    
    public boolean isCatch() {
    	return _isCatch;
    }


}
