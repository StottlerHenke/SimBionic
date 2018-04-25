
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
    

public class ConditionSAXReader extends Parser {


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

  /** any order, minOccurs=, type=xsd:string **/
  public static String comment = "comment";
  /** id to refer to internally refer to the comment tag **/
  public static int comment_ID = 5;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String labelMode = "labelMode";
  /** id to refer to internally refer to the labelMode tag **/
  public static int labelMode_ID = 6;

  /** any order, minOccurs=, type=BindingGroup **/
  public static String bindings = "bindings";
  /** id to refer to internally refer to the bindings tag **/
  public static int bindings_ID = 7;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Condition readObject;

    
  /** constructor **/
  public ConditionSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Condition ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Condition getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
	 if (ConditionSAXReader.id.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConditionSAXReader.id_ID)) ;
     }
     else    
     
			     if (ConditionSAXReader.expr.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ConditionSAXReader.expr_ID)) ;
			     }
			     else 
     		
	 if (ConditionSAXReader.cx.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConditionSAXReader.cx_ID)) ;
     }
     else    
     
	 if (ConditionSAXReader.cy.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConditionSAXReader.cy_ID)) ;
     }
     else    
     
			     if (ConditionSAXReader.comment.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ConditionSAXReader.comment_ID)) ;
			     }
			     else 
     		
	 if (ConditionSAXReader.labelMode.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConditionSAXReader.labelMode_ID)) ;
     }
     else    
     
     if (ConditionSAXReader.bindings.equals(tag)) {
       stackParser.addParser(new BindingGroupSAXReader (stackParser,tag,tagAttributes,this,ConditionSAXReader.bindings_ID));
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
      
	case 1: //case ConditionSAXReader.id_ID:
       	    readObject.setId((Integer)result);
			break;    
     
     		case 2: //case ConditionSAXReader.expr_ID:
       	    readObject.setExpr((String)result);
			break;
     		
	case 3: //case ConditionSAXReader.cx_ID:
       	    readObject.setCx((Integer)result);
			break;    
     
	case 4: //case ConditionSAXReader.cy_ID:
       	    readObject.setCy((Integer)result);
			break;    
     
     		case 5: //case ConditionSAXReader.comment_ID:
       	    readObject.setComment((String)result);
			break;
     		
	case 6: //case ConditionSAXReader.labelMode_ID:
       	    readObject.setLabelMode((Integer)result);
			break;    
     
       case 7: //case ConditionSAXReader.bindings_ID
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
 
