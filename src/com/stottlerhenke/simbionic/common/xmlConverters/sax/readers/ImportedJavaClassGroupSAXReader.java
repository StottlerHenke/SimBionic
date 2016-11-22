
package com.stottlerhenke.simbionic.common.xmlConverters.sax.readers;
 /*
 * class automatically generated using XSLT translator
 *
 */

import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.StackParser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.basicParsers.*;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.readers.*;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.awt.Color;
    

public class ImportedJavaClassGroupSAXReader extends Parser {


  /** sequence ,minOccurs=0, type=xsd:string **/
  public static String importedJavaClass = "importedJavaClass";
   /** id to refer to internally refer to the importedJavaClass tag **/
  public static int importedJavaClass_ID = 1;
 
  protected String startTag;
  protected Hashtable startTagAttributes;
  List<String> readObjects;

    
  /** constructor **/
  public ImportedJavaClassGroupSAXReader (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObjects = new ArrayList();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  public List<String> getValue () {
	  return readObjects;
  }
  
 /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception { 
    stackParser.addParser(new StringParser(tag,tagAttributes,this,0)); 
  }
  
  public void endElement(String tag) throws Exception {
	  if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error
	 }
  }
  
  /** collect the result. the property argument is disregarded **/
  protected  void receiveParsingResult(int property, Object result) {
   try{
  	if (result == null) return;
  	readObjects.add((String)result);
   }
    catch(Exception e){
    	e.printStackTrace();
    }
  }


 } 
 
