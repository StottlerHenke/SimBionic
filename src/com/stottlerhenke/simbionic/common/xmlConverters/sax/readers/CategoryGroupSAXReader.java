
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
    

public class CategoryGroupSAXReader extends Parser {


  /** sequence ,minOccurs=0, type=Category **/
  public static String category = "category";
   /** id to refer to internally refer to the category tag **/
  public static int category_ID = 1;
 
  protected String startTag;
  protected Hashtable startTagAttributes;
  List<com.stottlerhenke.simbionic.common.xmlConverters.model.Category> readObjects;

    
  /** constructor **/
  public CategoryGroupSAXReader (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObjects = new   ArrayList<com.stottlerhenke.simbionic.common.xmlConverters.model.Category> ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  public List<com.stottlerhenke.simbionic.common.xmlConverters.model.Category> getValue () {
	  return readObjects;
  }
  
 /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception { 
	  
			stackParser.addParser(new CategorySAXReader(stackParser,tag,tagAttributes,this,0)); 
		 
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
  	readObjects.add((com.stottlerhenke.simbionic.common.xmlConverters.model.Category)result);
   }
    catch(Exception e){
    	e.printStackTrace();
    }
  }


 } 
 
