
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
    

public class ConstantFolderGroupSAXReader extends Parser {


  /** sequence ,minOccurs=, type=Constant **/
  public static String constant = "constant";
   /** id to refer to internally refer to the constant tag **/
  public static int constant_ID = 1;
 
  /** sequence ,minOccurs=, type=ConstantFolder **/
  public static String constantFolder = "constantFolder";
   /** id to refer to internally refer to the constantFolder tag **/
  public static int constantFolder_ID = 2;
 
 
  protected String startTag;
  protected Hashtable startTagAttributes;
  com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolderGroup readObjects; 
  
  /** constructor **/
  public ConstantFolderGroupSAXReader (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObjects = new com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolderGroup();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  public com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolderGroup getValue () {
	  return readObjects;
  }    
 
 /** given the start of a tag create a parser to transform the content of the tag into a TG object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {
    
    if (ConstantFolderGroupSAXReader.constant.equals(tag)) {
      stackParser.addParser(new ConstantSAXReader(stackParser,tag,tagAttributes,this,0));
      return;
    }
       
    if (ConstantFolderGroupSAXReader.constantFolder.equals(tag)) {
      stackParser.addParser(new ConstantFolderSAXReader(stackParser,tag,tagAttributes,this,0));
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
	 readObjects.getConstantOrConstantFolder().add(result);
   }
    catch(Exception e){
    	e.printStackTrace();
    }  	
  }
  

 } 
 
