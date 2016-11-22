
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



abstract public class Node implements Serializable {
   
    public static final int INVALID_CX = -1;
    public static final int INVALID_CY = -1;
    

    protected int id;
    protected int cx = INVALID_CX;
    protected int cy = INVALID_CY;
    protected String comment = "";
    protected int labelMode;
    protected List<Binding> bindings = new ArrayList<Binding>();

    
    public Node() {
    }
    
    /**
    * Copy constructor.
    * @param n the Node to copy.
    */
   public Node(Node n) {
      id = n.getId();
      cx = n.getCx();
      cy = n.getCy();
      comment = n.getComment();
      labelMode = n.getLabelMode();
      for (Binding b : n.getBindings()) {
	 bindings.add(new Binding(b));
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
     * Gets the value of the cx property.
     * 
     */
    public int getCx() {
        return cx;
    }

    /**
     * Sets the value of the cx property.
     * 
     */
    public void setCx(int value) {
        this.cx = value;
    }

    /**
     * Gets the value of the cy property.
     * 
     */
    public int getCy() {
        return cy;
    }

    /**
     * Sets the value of the cy property.
     * 
     */
    public void setCy(int value) {
        this.cy = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the labelMode property.
     * 
     */
    public int getLabelMode() {
        return labelMode;
    }

    /**
     * Sets the value of the labelMode property.
     * 
     */
    public void setLabelMode(int value) {
        this.labelMode = value;
    }

   

    /**
     * Gets the value of the bindings property.
     * 
     * @return
     *     possible object is
     *     {@link List<Binding> }
     *     
     */
    public List<Binding> getBindings() {
        return bindings;
    }

    /**
     * Sets the value of the bindings property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Binding> }
     *     
     */
    public void setBindings(List<Binding> value) {
        this.bindings = value;
    }
    
    public void addBinding(Binding binding) {
       bindings.add(binding);
    }
    
    public void addBinding(int index, Binding binding) {
       bindings.add(index, binding);
    }
    
    public void removeBinding(Binding binding) {
       bindings.remove(binding);
    }
    
    public void removeBinding(int index) {
       bindings.remove(index);
    }
    
    public void clearBindings() {
       bindings.clear();
    }

}
