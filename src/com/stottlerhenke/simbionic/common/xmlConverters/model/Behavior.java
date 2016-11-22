
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Behavior complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Behavior">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="exec" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="interrupt" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="parameters" type="{}List<Parameter>" minOccurs="0"/>
 *         &lt;element name="polys" type="{}List<Poly>"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Behavior", propOrder = {

})
public class Behavior extends Function{


   protected int exec;
   protected boolean interrupt;
   @XmlElement(required = true)
   protected List<Poly> polys = new ArrayList<Poly>();

   public Behavior() {
   }

   /**
    * Copy constructor.
    * @param b the Behavior to copy.
    */
   public Behavior(Behavior b) {
      super(b);
      exec = b.getExec();
      interrupt = b.isInterrupt();

      for (Poly p : b.getPolys()) {
	 polys.add(new Poly(p));
      }

   }


   /**
    * Gets the value of the exec property.
    * 
    */
   public int getExec() {
      return exec;
   }

   /**
    * Sets the value of the exec property.
    * 
    */
   public void setExec(int value) {
      this.exec = value;
   }

   /**
    * Gets the value of the interrupt property.
    * 
    */
   public boolean isInterrupt() {
      return interrupt;
   }

   /**
    * Sets the value of the interrupt property.
    * 
    */
   public void setInterrupt(boolean value) {
      this.interrupt = value;
   }


   /**
    * Gets the value of the polys property.
    * 
    * @return
    *     possible object is
    *     {@link List<Poly> }
    *     
    */
   public List<Poly> getPolys() {
      return polys;
   }

   /**
    * Sets the value of the polys property.
    * 
    * @param value
    *     allowed object is
    *     {@link List<Poly> }
    *     
    */
   public void setPolys(List<Poly> value) {
      this.polys = value;
   }

   public void clearPolys() {
      this.polys.clear();
   }

   public void addPoly(Poly poly) {
      polys.add(poly);
   }

   public void removePoly(Poly poly) {
      polys.remove(poly);
   }

   public void swapPoly(int i, int j)
   {
      polys.set(j, polys.set(i, polys.get(j)));
   }
   
   public void insertPoly(int index, Poly poly) {
      polys.add(index, poly);
   }



}
