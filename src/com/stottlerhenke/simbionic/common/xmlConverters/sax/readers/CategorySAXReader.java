
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
    

public class CategorySAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:string **/
  public static String name = "name";
  /** id to refer to internally refer to the name tag **/
  public static int name_ID = 1;

  /** any order, minOccurs=, type=DescriptorGroup **/
  public static String descriptors = "descriptors";
  /** id to refer to internally refer to the descriptors tag **/
  public static int descriptors_ID = 2;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String selected = "selected";
  /** id to refer to internally refer to the selected tag **/
  public static int selected_ID = 3;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Category readObject;

    
  /** constructor **/
  public CategorySAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Category ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Category getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
			     if (CategorySAXReader.name.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,CategorySAXReader.name_ID)) ;
			     }
			     else 
     		
     if (CategorySAXReader.descriptors.equals(tag)) {
       stackParser.addParser(new DescriptorGroupSAXReader (stackParser,tag,tagAttributes,this,CategorySAXReader.descriptors_ID));
     }
    else
    

	 if (CategorySAXReader.selected.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,CategorySAXReader.selected_ID)) ;
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
      
     		case 1: //case CategorySAXReader.name_ID:
       	    readObject.setName((String)result);
			break;
     		
       case 2: //case CategorySAXReader.descriptors_ID
            if (result !=null) {
              readObject.setDescriptors((List)result);
            }
           
          break;    

	case 3: //case CategorySAXReader.selected_ID:
       	    readObject.setSelected((Boolean)result);
  			break;    
          
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  

 } 
 
