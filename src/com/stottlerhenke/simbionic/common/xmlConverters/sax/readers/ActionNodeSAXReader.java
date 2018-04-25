
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
    

public class ActionNodeSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:integer **/
  public static String id = "id";
  /** id to refer to internally refer to the id tag **/
  public static int id_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String expr = "expr";
  /** id to refer to internally refer to the expr tag **/
  public static int expr_ID = 2;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String cx = "cx";
  /** id to refer to internally refer to the cx tag **/
  public static int cx_ID = 3;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String cy = "cy";
  /** id to refer to internally refer to the cy tag **/
  public static int cy_ID = 4;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String width = "width";
  /** id to refer to internally refer to the width tag **/
  public static int width_ID = 5;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String height = "height";
  /** id to refer to internally refer to the height tag **/
  public static int height_ID = 6;

  /** any order, minOccurs=, type=xsd:string **/
  public static String comment = "comment";
  /** id to refer to internally refer to the comment tag **/
  public static int comment_ID = 7;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String labelMode = "labelMode";
  /** id to refer to internally refer to the labelMode tag **/
  public static int labelMode_ID = 8;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String isFinal = "isFinal";
  /** id to refer to internally refer to the isFinal tag **/
  public static int isFinal_ID = 9;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String isBehavior = "isBehavior";
  /** id to refer to internally refer to the isBehavior tag **/
  public static int isBehavior_ID = 10;

  /** any order, minOccurs=, type=BindingGroup **/
  public static String bindings = "bindings";
  /** id to refer to internally refer to the bindings tag **/
  public static int bindings_ID = 11;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String isAlways = "isAlways";
  /** id to refer to internally refer to the isAlways tag **/
  public static int isAlways_ID = 12;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String isCatch = "isCatch";
  /** id to refer to internally refer to the isCatch tag **/
  public static int isCatch_ID = 13;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode readObject;

    
  /** constructor **/
  public ActionNodeSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
	 if (ActionNodeSAXReader.id.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ActionNodeSAXReader.id_ID)) ;
     }
     else    
     
			     if (ActionNodeSAXReader.expr.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ActionNodeSAXReader.expr_ID)) ;
			     }
			     else 
     		
	 if (ActionNodeSAXReader.cx.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ActionNodeSAXReader.cx_ID)) ;
     }
     else    
     
	 if (ActionNodeSAXReader.cy.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ActionNodeSAXReader.cy_ID)) ;
     }
     else    
     
	 if (ActionNodeSAXReader.width.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ActionNodeSAXReader.width_ID)) ;
     }
     else    
     
	 if (ActionNodeSAXReader.height.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ActionNodeSAXReader.height_ID)) ;
     }
     else    
     
			     if (ActionNodeSAXReader.comment.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ActionNodeSAXReader.comment_ID)) ;
			     }
			     else 
     		
	 if (ActionNodeSAXReader.labelMode.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ActionNodeSAXReader.labelMode_ID)) ;
     }
     else    
     
	 if (ActionNodeSAXReader.isFinal.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,ActionNodeSAXReader.isFinal_ID)) ;
     }
     
     else    
     
	 if (ActionNodeSAXReader.isBehavior.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,ActionNodeSAXReader.isBehavior_ID)) ;
     }
     
     else    
     
     if (ActionNodeSAXReader.bindings.equals(tag)) {
       stackParser.addParser(new BindingGroupSAXReader (stackParser,tag,tagAttributes,this,ActionNodeSAXReader.bindings_ID));
     }
    else
    

	 if (ActionNodeSAXReader.isAlways.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,ActionNodeSAXReader.isAlways_ID)) ;
     }
     
     else    
     
	 if (ActionNodeSAXReader.isCatch.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,ActionNodeSAXReader.isCatch_ID)) ;
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
      
	case 1: //case ActionNodeSAXReader.id_ID:
       	    readObject.setId((Integer)result);
			break;    
     
     		case 2: //case ActionNodeSAXReader.expr_ID:
       	    readObject.setExpr((String)result);
			break;
     		
	case 3: //case ActionNodeSAXReader.cx_ID:
       	    readObject.setCx((Integer)result);
			break;    
     
	case 4: //case ActionNodeSAXReader.cy_ID:
       	    readObject.setCy((Integer)result);
			break;    
     
	case 5: //case ActionNodeSAXReader.width_ID:
       	    readObject.setWidth((Integer)result);
			break;    
     
	case 6: //case ActionNodeSAXReader.height_ID:
       	    readObject.setHeight((Integer)result);
			break;    
     
     		case 7: //case ActionNodeSAXReader.comment_ID:
       	    readObject.setComment((String)result);
			break;
     		
	case 8: //case ActionNodeSAXReader.labelMode_ID:
       	    readObject.setLabelMode((Integer)result);
			break;    
     
	case 9: //case ActionNodeSAXReader.isFinal_ID:
       	    readObject.setIsFinal((Boolean)result);
  			break;    
     
	case 10: //case ActionNodeSAXReader.isBehavior_ID:
       	    readObject.setIsBehavior((Boolean)result);
  			break;    
     
       case 11: //case ActionNodeSAXReader.bindings_ID
            if (result !=null) {
              readObject.setBindings((List)result);
            }
           
          break;    

	case 12: //case ActionNodeSAXReader.isAlways_ID:
       	    readObject.setAlways((Boolean)result);
  			break;    
     
	case 13: //case ActionNodeSAXReader.isCatch_ID:
       	    readObject.setCatch((Boolean)result);
  			break;    
          
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  

 } 
 
