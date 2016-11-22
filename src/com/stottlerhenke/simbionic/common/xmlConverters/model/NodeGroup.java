
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NodeGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NodeGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="initial" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="actionNodes" type="{}List<ActionNode>"/>
 *         &lt;element name="compoundActionNode" type="{}List<CompoundActionNode>"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NodeGroup", propOrder = {

})
public class NodeGroup implements Serializable {

    protected int initial = -1;
    @XmlElement(required = true)
    protected List<ActionNode> actionNodes = new ArrayList<ActionNode>();
    @XmlElement(required = true)
    protected List<CompoundActionNode> compoundActionNodes = new ArrayList<CompoundActionNode>();


    public NodeGroup() {
    }
    
    
    /**
    * Copy constructor.
    * @param ng the NodeGroup to copy.
    */
   public NodeGroup(NodeGroup ng) {
      initial = ng.getInitial();
      for (ActionNode a : ng.getActionNodes()) {
	 actionNodes.add(new ActionNode(a));
      }
      
      for (CompoundActionNode a : ng.getCompoundActionNodes()) {
	 compoundActionNodes.add(new CompoundActionNode(a));
      }
       
    }
    
    
    
    /**
     * Gets the value of the initial property.
     * 
     */
    public int getInitial() {
        return initial;
    }

    /**
     * Sets the value of the initial property.
     * 
     */
    public void setInitial(int value) {
        this.initial = value;
    }

    /**
     * Gets the value of the actionNodes property.
     * 
     * @return
     *     possible object is
     *     {@link List<ActionNode> }
     *     
     */
    public List<ActionNode> getActionNodes() {
        return actionNodes;
    }

    /**
     * Sets the value of the actionNodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<ActionNode> }
     *     
     */
    public void setActionNodes(List<ActionNode> value) {
        this.actionNodes = value;
    }
    
    public void addActionNode(ActionNode actionNode) {
       actionNodes.add(actionNode);
    }
    
    public void removeActionNode(ActionNode actionNode) {
       actionNodes.remove(actionNode);
    }

    /**
     * Gets the value of the compoundActionNodes property.
     * 
     * @return
     *     possible object is
     *     {@link List<CompoundActionNode> }
     *     
     */
    public List<CompoundActionNode> getCompoundActionNodes() {
        return compoundActionNodes;
    }

    /**
     * Sets the value of the compoundActionNodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<CompoundActionNode> }
     *     
     */
    public void setCompoundActionNodes(List<CompoundActionNode> value) {
        this.compoundActionNodes = value;
    }
    
    public void addCompoundActionNode(CompoundActionNode compoundActionNode) {
       compoundActionNodes.add(compoundActionNode);
    }
    
    public void removeCompoundActionNode(CompoundActionNode compoundActionNode) {
       compoundActionNodes.remove(compoundActionNode);
    }

}
