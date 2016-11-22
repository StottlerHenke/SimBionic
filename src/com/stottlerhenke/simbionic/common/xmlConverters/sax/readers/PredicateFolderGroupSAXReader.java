
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
    

public class PredicateFolderGroupSAXReader extends Parser {


  /** sequence ,minOccurs=, type=Predicate **/
  public static String predicate = "predicate";
   /** id to refer to internally refer to the predicate tag **/
  public static int predicate_ID = 1;
 
  /** sequence ,minOccurs=, type=PredicateFolder **/
  public static String predicateFolder = "predicateFolder";
   /** id to refer to internally refer to the predicateFolder tag **/
  public static int predicateFolder_ID = 2;
 
 
  protected String startTag;
  protected Hashtable startTagAttributes;
  com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup readObjects;
    
  /** constructor **/
  public PredicateFolderGroupSAXReader (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObjects = new com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  public com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup getValue () {
	  return readObjects;
  } 
 
 /** given the start of a tag create a parser to transform the content of the tag into a TG object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {
    
    if (PredicateFolderGroupSAXReader.predicate.equals(tag)) {
      stackParser.addParser(new PredicateSAXReader(stackParser,tag,tagAttributes,this,0));
      return;
    }
       
    if (PredicateFolderGroupSAXReader.predicateFolder.equals(tag)) {
      stackParser.addParser(new PredicateFolderSAXReader(stackParser,tag,tagAttributes,this,0));
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
  	readObjects.getPredicateOrPredicateFolder().add(result);
   }
    catch(Exception e){
    	e.printStackTrace();
    }  	
  }
  

 } 
 
