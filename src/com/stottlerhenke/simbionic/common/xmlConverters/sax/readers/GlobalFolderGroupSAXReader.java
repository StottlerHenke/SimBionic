
package com.stottlerhenke.simbionic.common.xmlConverters.sax.readers;
 /*
 * class automatically generated using XSLT translator
 *
 */

import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.StackParser;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.awt.Color;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.basicParsers.*;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.readers.*;
    

public class GlobalFolderGroupSAXReader extends Parser {


  /** sequence ,minOccurs=, type=Global **/
  public static String global = "global";
   /** id to refer to internally refer to the global tag **/
  public static int global_ID = 1;
 
  /** sequence ,minOccurs=, type=GlobalFolder **/
  public static String globalFolder = "globalFolder";
   /** id to refer to internally refer to the globalFolder tag **/
  public static int globalFolder_ID = 2;
 
 
  protected String startTag;
  protected Hashtable startTagAttributes;
  com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolderGroup readObjects; 
  
  /** constructor **/
  public GlobalFolderGroupSAXReader (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObjects = new com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolderGroup();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  public com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolderGroup getValue () {
	  return readObjects;
  }    
 
 /** given the start of a tag create a parser to transform the content of the tag into a TG object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {
    
    if (GlobalFolderGroupSAXReader.global.equals(tag)) {
      stackParser.addParser(new GlobalSAXReader(stackParser,tag,tagAttributes,this,0));
      return;
    }
       
    if (GlobalFolderGroupSAXReader.globalFolder.equals(tag)) {
      stackParser.addParser(new GlobalFolderSAXReader(stackParser,tag,tagAttributes,this,0));
      return;
    }
          
  }
  
    public void endElement(String tag) throws Exception {		
	  if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error
	 }
  }
 
  /** collect the result . The property argument is disregarded **/ 
  protected  void receiveParsingResult(int property, Object result) {
   try{
    if (result == null) return;
	 readObjects.getGlobalOrGlobalFolder().add(result);
   }
    catch(Exception e){
    	e.printStackTrace();
    }  	
  }
  

 } 
 
