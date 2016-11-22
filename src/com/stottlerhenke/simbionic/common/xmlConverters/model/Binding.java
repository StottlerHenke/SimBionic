
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Binding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Binding">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="var" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Binding", propOrder = {

})
public class Binding implements Serializable{

   @XmlElement(required = true)
   protected String var = "";
   @XmlElement(required = true)
   protected String expr = "";


   public Binding() {

   }


   /**
    * Copy constructor.
    * @param b the Binding to copy.
    */
   public Binding(Binding b) {
      var = b.getVar();
      expr = b.getExpr();
   }

   /**
    * Gets the value of the var property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getVar() {
      return var;
   }

   /**
    * Sets the value of the var property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setVar(String value) {
      this.var = value;
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
      if (value == null) 
	 this.expr = "";
	 else this.expr = value;
   }

}
