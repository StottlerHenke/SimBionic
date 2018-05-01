
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
    

public class BehaviorSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:string **/
  public static String name = "name";
  /** id to refer to internally refer to the name tag **/
  public static int name_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String description = "description";
  /** id to refer to internally refer to the description tag **/
  public static int description_ID = 2;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String exec = "exec";
  /** id to refer to internally refer to the exec tag **/
  public static int exec_ID = 3;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String interrupt = "interrupt";
  /** id to refer to internally refer to the interrupt tag **/
  public static int interrupt_ID = 4;

  /** any order, minOccurs=0, type=ParameterGroup **/
  public static String parameters = "parameters";
  /** id to refer to internally refer to the parameters tag **/
  public static int parameters_ID = 5;

  /** any order, minOccurs=, type=PolyGroup **/
  public static String polys = "polys";
  /** id to refer to internally refer to the polys tag **/
  public static int polys_ID = 6;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior readObject;

    
  /** constructor **/
  public BehaviorSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
			     if (BehaviorSAXReader.name.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,BehaviorSAXReader.name_ID)) ;
			     }
			     else 
     		
			     if (BehaviorSAXReader.description.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,BehaviorSAXReader.description_ID)) ;
			     }
			     else 
     		
	 if (BehaviorSAXReader.exec.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,BehaviorSAXReader.exec_ID)) ;
     }
     else    
     
	 if (BehaviorSAXReader.interrupt.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,BehaviorSAXReader.interrupt_ID)) ;
     }
     
     else    
     
     if (BehaviorSAXReader.parameters.equals(tag)) {
       stackParser.addParser(new ParameterGroupSAXReader (stackParser,tag,tagAttributes,this,BehaviorSAXReader.parameters_ID));
     }
    else
    

     if (BehaviorSAXReader.polys.equals(tag)) {
       stackParser.addParser(new PolyGroupSAXReader (stackParser,tag,tagAttributes,this,BehaviorSAXReader.polys_ID));
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
      
     		case 1: //case BehaviorSAXReader.name_ID:
       	    readObject.setName((String)result);
			break;
     		
     		case 2: //case BehaviorSAXReader.description_ID:
       	    readObject.setDescription((String)result);
			break;
     		
	case 3: //case BehaviorSAXReader.exec_ID:
       	    readObject.setExec((Integer)result);
			break;    
     
	case 4: //case BehaviorSAXReader.interrupt_ID:
       	    readObject.setInterrupt((Boolean)result);
  			break;    
     
       case 5: //case BehaviorSAXReader.parameters_ID
            if (result !=null) {
              readObject.setParameters((List)result);
            }
           
          break;    

       case 6: //case BehaviorSAXReader.polys_ID
            if (result !=null) {
              readObject.setPolys((List)result);
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
 
