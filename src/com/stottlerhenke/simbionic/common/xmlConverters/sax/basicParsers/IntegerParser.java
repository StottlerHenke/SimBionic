package com.stottlerhenke.simbionic.common.xmlConverters.sax.basicParsers;


import java.util.Hashtable;

import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;

/**
 * parser a simple tag of the for <tagName>value</tagName>
 * 
 *
 */
public class IntegerParser extends Parser {

   public IntegerParser(String tagName,  Hashtable h,Parser client, int property) {
      super(null,client,property);
      tagBeingRead = tagName;
   }
   
   public void text(String str) throws Exception {
      readValue = Integer.parseInt(str);
   }
   
   public void endElement(String tag) throws Exception {
      if (tagBeingRead.equalsIgnoreCase(tag)) {
         isDone = true;
      }
   }
   
   public Integer getValue() {
      return readValue;
   }
   
   Integer readValue = null;
   String tagBeingRead = null;
}

