package com.stottlerhenke.simbionic.common.xmlConverters.sax.basicParsers;


import java.util.Hashtable;

import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;

/**
 * parser a simple tag of the for <tagName>value</tagName>
 *
 */
public class StringParser extends Parser {

   public StringParser(String tagName,  Hashtable h, Parser client, int property) {
      super(null,client,property);
      tagBeingRead = tagName;
   }
   
   public void text(String str) throws Exception {
      readValue = str;
   }
   
   public void endElement(String tag) throws Exception {
      if (tagBeingRead.equalsIgnoreCase(tag)) {
         isDone = true;
      }
   }
   
   public String getValue() {
      return readValue;
   }
   
   String readValue = null;
   String tagBeingRead = null;
}

