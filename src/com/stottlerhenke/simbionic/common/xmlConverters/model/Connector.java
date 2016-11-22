package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.stottlerhenke.simbionic.editor.gui.SB_Element;


/**
 * <p>Java class for Connector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Connector">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="endId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="endType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="startX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="startY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="endX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="endY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="interrupt" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="labelMode" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "Connector", propOrder = {

})
public class Connector implements Serializable {

    protected int id;
    protected int endId;
    protected int endType;
    protected int startX;
    protected int startY;
    protected int endX;
    protected int endY;
    protected int priority;
    protected boolean interrupt;
    @XmlElement(required = true)
    protected String comment = "";
    protected int labelMode = SB_Element.TRUNCATED_LABEL;
    @XmlElement(required = true)
    protected List<Binding> bindings = new ArrayList<Binding>();

    
    public Connector() {
    }
    
    /**
    * Copy constructor. 
    * @param c the Connector to copy.
    */
   public Connector(Connector c) {
       id = c.getId();
       endId = c.getEndId();
       endType = c.getEndType();
       startX = c.getStartX();
       startY = c.getStartY();
       endX = c.getEndX();
       endY = c.getEndY();
       priority = c.getPriority();
       interrupt = c.isInterrupt();
       comment = c.getComment();
       labelMode = c.getLabelMode();
       
       for (Binding b : c.getBindings()) {
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
     * Gets the value of the endId property.
     * 
     */
    public int getEndId() {
        return endId;
    }

    /**
     * Sets the value of the endId property.
     * 
     */
    public void setEndId(int value) {
        this.endId = value;
    }

    /**
     * Gets the value of the endType property.
     * 
     */
    public int getEndType() {
        return endType;
    }

    /**
     * Sets the value of the endType property.
     * 
     */
    public void setEndType(int value) {
        this.endType = value;
    }

    /**
     * Gets the value of the startX property.
     * 
     */
    public int getStartX() {
        return startX;
    }

    /**
     * Sets the value of the startX property.
     * 
     */
    public void setStartX(int value) {
        this.startX = value;
    }

    /**
     * Gets the value of the startY property.
     * 
     */
    public int getStartY() {
        return startY;
    }

    /**
     * Sets the value of the startY property.
     * 
     */
    public void setStartY(int value) {
        this.startY = value;
    }
    
    /**
     * Gets the value of the endX property.
     * 
     */
    public int getEndX() {
        return endX;
    }

    /**
     * Sets the value of the endX property.
     * 
     */
    public void setEndX(int value) {
        this.endX = value;
    }

    /**
     * Gets the value of the endY property.
     * 
     */
    public int getEndY() {
        return endY;
    }

    /**
     * Sets the value of the endY property.
     * 
     */
    public void setEndY(int value) {
        this.endY = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     */
    public void setPriority(int value) {
        this.priority = value;
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
