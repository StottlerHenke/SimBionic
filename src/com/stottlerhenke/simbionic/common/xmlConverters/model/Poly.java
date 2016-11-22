
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Poly complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Poly">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="indices" type="{}List<String>"/>
 *         &lt;element name="locals" type="{}List<Local>"/>
 *         &lt;element name="nodes" type="{}NodeGroup"/>
 *         &lt;element name="conditions" type="{}List<Condition>"/>
 *         &lt;element name="connectors" type="{}List<Start>"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Poly", propOrder = {

})
public class Poly implements Serializable {

    @XmlElement(required = true)
    protected List<String> indices = new ArrayList<String>();
    @XmlElement(required = true)
    protected List<Local> locals = new ArrayList<Local>();
    @XmlElement(required = true)
    protected NodeGroup nodes = new NodeGroup();
    @XmlElement(required = true)
    protected List<Condition> conditions = new ArrayList<Condition>();
    @XmlElement(required = true)
    protected List<Start> connectors = new ArrayList<Start>();

    
    public Poly(){
    }
    
    /** 
    * Copy constructor.
    * @param p the Poly to copy.
    */
    public Poly(Poly p) {

       for (String s : p.getIndices()) {
	  indices.add(s);
       }

       for (Local l : p.getLocals()) {
	  locals.add(new Local(l));
       }
       
       nodes = new NodeGroup(p.getNodes());
       
       for (Condition c : p.getConditions()) {
	  conditions.add(new Condition(c));
       }
       
       for (Start s : p.getConnectors()) {
	  connectors.add(new Start(s));
       }



    }
    
    
    /**
     * Gets the value of the indices property.
     * 
     * @return
     *     possible object is
     *     {@link List<String> }
     *     
     */
    public List<String> getIndices() {
        return indices;
    }

    /**
     * Sets the value of the indices property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<String> }
     *     
     */
    public void setIndices(List<String> value) {
        this.indices = value;
    }

    /**
     * Gets the value of the locals property.
     * 
     * @return
     *     possible object is
     *     {@link List<Local> }
     *     
     */
    public List<Local> getLocals() {
        return locals;
    }

    /**
     * Sets the value of the locals property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Local> }
     *     
     */
    public void setLocals(List<Local> value) {
        this.locals = value;
    }
    
    public void addLocal(Local local) {
       locals.add(local);
    }
    
    public void addLocal(int index, Local local) {
       locals.add(index, local);
    }
    
    public void removeLocal(Local local) {
       locals.remove(local);
    }

    /**
     * Gets the value of the nodes property.
     * 
     * @return
     *     possible object is
     *     {@link NodeGroup }
     *     
     */
    public NodeGroup getNodes() {
        return nodes;
    }

    /**
     * Sets the value of the nodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeGroup }
     *     
     */
    public void setNodes(NodeGroup value) {
        this.nodes = value;
    }

    /**
     * Gets the value of the conditions property.
     * 
     * @return
     *     possible object is
     *     {@link List<Condition>  }
     *     
     */
    public List<Condition>  getConditions() {
        return conditions;
    }

    /**
     * Sets the value of the conditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Condition>  }
     *     
     */
    public void setConditions(List<Condition>  value) {
        this.conditions = value;
    }
    
    public void addCondition(Condition condition) {
       conditions.add(condition);
    }
    
    public void removeCondition(Condition condition) {
       conditions.remove(condition);
    }

    /**
     * Gets the value of the connectors property.
     * 
     * @return
     *     possible object is
     *     {@link List<Start>  }
     *     
     */
    public List<Start> getConnectors() {
        return connectors;
    }

    /**
     * Sets the value of the connectors property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Start> }
     *     
     */
    public void setConnectors(List<Start> value) {
        this.connectors = value;
    }
    
    public void addConnector(Start connector) {
       connectors.add(connector);
    }
    
    public void removeConnector(Start connector) {
       connectors.remove(connector);
    }
    
    /**
     * Get the Start connector object based on the start id and type.
     * If not found, create a new start connector, add to the connectors, 
     * then return the newly created connector.
     * @param startId The start id.
     * @param startType The start type
     * @return 
     */
    public Start getConnector(int startId, int startType) {
       for (Start startModel : connectors) {
          if (startModel.getId() == startId && startModel.getType() == startType) {
             return startModel;
          }
       }
       
       // not found, create one
       Start newStartModel = new Start();
       newStartModel.setId(startId);
       newStartModel.setType(startType);
       connectors.add(newStartModel);
       return newStartModel;
    }
    
    


}
