
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
    

public class ProjectPropertiesSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:string **/
  public static String author = "author";
  /** id to refer to internally refer to the author tag **/
  public static int author_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String projectName = "projectName";
  /** id to refer to internally refer to the projectName tag **/
  public static int projectName_ID = 2;

  /** any order, minOccurs=, type=xsd:string **/
  public static String description = "description";
  /** id to refer to internally refer to the description tag **/
  public static int description_ID = 3;

  /** any order, minOccurs=, type=xsd:string **/
  public static String dateLastUpdate = "dateLastUpdate";
  /** id to refer to internally refer to the dateLastUpdate tag **/
  public static int dateLastUpdate_ID = 4;

  /** any order, minOccurs=, type=xsd:string **/
  public static String simbionicVersion = "simbionicVersion";
  /** id to refer to internally refer to the simbionicVersion tag **/
  public static int simbionicVersion_ID = 5;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.ProjectProperties readObject;

    
  /** constructor **/
  public ProjectPropertiesSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.ProjectProperties ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.ProjectProperties getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
			     if (ProjectPropertiesSAXReader.author.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ProjectPropertiesSAXReader.author_ID)) ;
			     }
			     else 
     		
			     if (ProjectPropertiesSAXReader.projectName.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ProjectPropertiesSAXReader.projectName_ID)) ;
			     }
			     else 
     		
			     if (ProjectPropertiesSAXReader.description.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ProjectPropertiesSAXReader.description_ID)) ;
			     }
			     else 
     		
			     if (ProjectPropertiesSAXReader.dateLastUpdate.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ProjectPropertiesSAXReader.dateLastUpdate_ID)) ;
			     }
			     else 
     		
			     if (ProjectPropertiesSAXReader.simbionicVersion.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ProjectPropertiesSAXReader.simbionicVersion_ID)) ;
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
      
     		case 1: //case ProjectPropertiesSAXReader.author_ID:
       	    readObject.setAuthor((String)result);
			break;
     		
     		case 2: //case ProjectPropertiesSAXReader.projectName_ID:
       	    readObject.setProjectName((String)result);
			break;
     		
     		case 3: //case ProjectPropertiesSAXReader.description_ID:
       	    readObject.setDescription((String)result);
			break;
     		
     		case 4: //case ProjectPropertiesSAXReader.dateLastUpdate_ID:
       	    readObject.setDateLastUpdate((String)result);
			break;
     		
     		case 5: //case ProjectPropertiesSAXReader.simbionicVersion_ID:
       	    readObject.setSimbionicVersion((String)result);
			break;
     		     
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  

 } 
 
