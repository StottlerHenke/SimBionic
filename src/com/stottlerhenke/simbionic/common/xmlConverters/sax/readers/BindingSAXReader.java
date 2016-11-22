
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
    

public class BindingSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:string **/
  public static String var = "var";
  /** id to refer to internally refer to the var tag **/
  public static int var_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String expr = "expr";
  /** id to refer to internally refer to the expr tag **/
  public static int expr_ID = 2;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Binding readObject;

    
  /** constructor **/
  public BindingSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Binding ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Binding getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
			     if (BindingSAXReader.var.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,BindingSAXReader.var_ID)) ;
			     }
			     else 
     		
			     if (BindingSAXReader.expr.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,BindingSAXReader.expr_ID)) ;
			     }
			     else 
     		
      {
      	//signal error
      }
      
  } //end of startElement method

  public void endElement(String tag) throws Exception {
	  if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error ?
	 }
  }
  
  /**  set the given field property of the object being read **/
  protected  void receiveParsingResult(int property, Object result) {
  	try{
     switch (property) {
      
     		case 1: //case BindingSAXReader.var_ID:
       	    readObject.setVar((String)result);
			break;
     		
     		case 2: //case BindingSAXReader.expr_ID:
       	    readObject.setExpr((String)result);
			break;
     		     
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  

 } 
 
