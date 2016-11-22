package com.stottlerhenke.simbionic.common.xmlConverters.model;

abstract public class Folder {

   protected String name;
   

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
}
