package com.stottlerhenke.simbionic.common.xmlConverters.sax.basicParsers;

import java.util.Hashtable;

import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;

/**
 * parser a simple tag of the for <tagName>value</tagName>
 * 
 *
 */
public class BooleanParser extends Parser {

   public BooleanParser(String tagName,  Hashtable h,Parser client, int property) {
      super(null,client,property);
      tagBeingRead = tagName;
   }
   
   public void text(String str) throws Exception {
      readValue = Boolean.parseBoolean(str);
   }
   
   public void endElement(String tag) throws Exception {
      if (tagBeingRead.equalsIgnoreCase(tag)) {
         isDone = true;
      }
   }
   
   public Boolean getValue() {
      return readValue;
   }
   
   Boolean readValue = null;
   String tagBeingRead = null;
}
