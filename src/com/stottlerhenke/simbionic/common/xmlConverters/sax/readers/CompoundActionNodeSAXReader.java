
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
    

public class CompoundActionNodeSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:integer **/
  public static String id = "id";
  /** id to refer to internally refer to the id tag **/
  public static int id_ID = 1;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String cx = "cx";
  /** id to refer to internally refer to the cx tag **/
  public static int cx_ID = 2;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String cy = "cy";
  /** id to refer to internally refer to the cy tag **/
  public static int cy_ID = 3;

  /** any order, minOccurs=, type=xsd:string **/
  public static String comment = "comment";
  /** id to refer to internally refer to the comment tag **/
  public static int comment_ID = 4;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String labelMode = "labelMode";
  /** id to refer to internally refer to the labelMode tag **/
  public static int labelMode_ID = 5;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String isFinal = "isFinal";
  /** id to refer to internally refer to the isFinal tag **/
  public static int isFinal_ID = 6;

  /** any order, minOccurs=, type=BindingGroup **/
  public static String bindings = "bindings";
  /** id to refer to internally refer to the bindings tag **/
  public static int bindings_ID = 7;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode readObject;

    
  /** constructor **/
  public CompoundActionNodeSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
	 if (CompoundActionNodeSAXReader.id.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,CompoundActionNodeSAXReader.id_ID)) ;
     }
     else    
     
	 if (CompoundActionNodeSAXReader.cx.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,CompoundActionNodeSAXReader.cx_ID)) ;
     }
     else    
     
	 if (CompoundActionNodeSAXReader.cy.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,CompoundActionNodeSAXReader.cy_ID)) ;
     }
     else    
     
			     if (CompoundActionNodeSAXReader.comment.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,CompoundActionNodeSAXReader.comment_ID)) ;
			     }
			     else 
     		
	 if (CompoundActionNodeSAXReader.labelMode.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,CompoundActionNodeSAXReader.labelMode_ID)) ;
     }
     else    
     
	 if (CompoundActionNodeSAXReader.isFinal.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,CompoundActionNodeSAXReader.isFinal_ID)) ;
     }
     
     else    
     
     if (CompoundActionNodeSAXReader.bindings.equals(tag)) {
       stackParser.addParser(new BindingGroupSAXReader (stackParser,tag,tagAttributes,this,CompoundActionNodeSAXReader.bindings_ID));
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
      
	case 1: //case CompoundActionNodeSAXReader.id_ID:
       	    readObject.setId((Integer)result);
			break;    
     
	case 2: //case CompoundActionNodeSAXReader.cx_ID:
       	    readObject.setCx((Integer)result);
			break;    
     
	case 3: //case CompoundActionNodeSAXReader.cy_ID:
       	    readObject.setCy((Integer)result);
			break;    
     
     		case 4: //case CompoundActionNodeSAXReader.comment_ID:
       	    readObject.setComment((String)result);
			break;
     		
	case 5: //case CompoundActionNodeSAXReader.labelMode_ID:
       	    readObject.setLabelMode((Integer)result);
			break;    
     
	case 6: //case CompoundActionNodeSAXReader.isFinal_ID:
       	    readObject.setIsFinal((Boolean)result);
  			break;    
     
       case 7: //case CompoundActionNodeSAXReader.bindings_ID
            if (result !=null) {
              readObject.setBindings((List)result);
            }
           
          break;    
     
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  

 } 
 
