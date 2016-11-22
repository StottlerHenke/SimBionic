
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
    

public class BehaviorFolderGroupSAXReader extends Parser {


  /** sequence ,minOccurs=, type=Behavior **/
  public static String behavior = "behavior";
   /** id to refer to internally refer to the behavior tag **/
  public static int behavior_ID = 1;
 
  /** sequence ,minOccurs=, type=BehaviorFolder **/
  public static String behaviorFolder = "behaviorFolder";
   /** id to refer to internally refer to the behaviorFolder tag **/
  public static int behaviorFolder_ID = 2;
 
 
  protected String startTag;
  protected Hashtable startTagAttributes;
  com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup readObjects;
    
  /** constructor **/
  public BehaviorFolderGroupSAXReader (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObjects = new com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  public com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup getValue () {
	  return readObjects;
  } 
 
 /** given the start of a tag create a parser to transform the content of the tag into a TG object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {
    
    if (BehaviorFolderGroupSAXReader.behavior.equals(tag)) {
      stackParser.addParser(new BehaviorSAXReader(stackParser,tag,tagAttributes,this,0));
      return;
    }
       
    if (BehaviorFolderGroupSAXReader.behaviorFolder.equals(tag)) {
      stackParser.addParser(new BehaviorFolderSAXReader(stackParser,tag,tagAttributes,this,0));
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
  	readObjects.getBehaviorOrBehaviorFolder().add(result);
   }
    catch(Exception e){
    	e.printStackTrace();
    }  	
  }
  

 } 
 
