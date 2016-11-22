package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

abstract public class Function {
   
   private String name;
   protected String description;
   protected List<Parameter> parameters = new ArrayList<Parameter>();


   public Function() {      
   }
   
   
   /**
    * Copy constructor.
    * @param f the Function to copy.
    */
   public Function(Function f) {
      name = f.getName();
      description = f.getDescription();
      for (Parameter p : f.getParameters()) {
	 parameters.add(new Parameter(p));
      }
      
   }
   
   /**
    * Gets the value of the name property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getName() {
       return name;
   }

   /**
    * Sets the value of the name property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setName(String value) {
       this.name = value;
   }

  
   /**
    * Gets the value of the description property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getDescription() {
       return description;
   }

   /**
    * Sets the value of the description property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setDescription(String value) {
       this.description = value;
   }


   /**
    * Gets the value of the parameters property.
    * 
    * @return
    *     possible object is
    *     {@link List<Parameter> }
    *     
    */
   public List<Parameter> getParameters() {
       return parameters;
   }

   /**
    * Sets the value of the parameters property.
    * 
    * @param value
    *     allowed object is
    *     {@link List<Parameter> }
    *     
    */
   public void setParameters(List<Parameter> value) {
       this.parameters = value;
   }
   
   public void addParameter(Parameter param) {
      parameters.add(param);
   }
   
   public void addParameter(int index, Parameter param) {
      parameters.add(index, param);
   }
   
   
   public void removeParameter(Parameter param) {
      parameters.remove(param);
   }

}
