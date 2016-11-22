
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompoundActionNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompoundActionNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cx" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cy" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="labelMode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="isFinal" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "CompoundActionNode", propOrder = {

})
public class CompoundActionNode extends ActionNode {
   
   public CompoundActionNode() { 
   }
   
   
   /**
    * Copy Constructor.
    * @param n the CompoundActionNode to copy.
    */
   public CompoundActionNode(CompoundActionNode n) {
      super(n);
   }
   

}
