
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
    

public class JavaScriptSAXReader extends Parser {


  /** any order, minOccurs=, type=JsFileGroup **/
  public static String jsFiles = "jsFiles";
  /** id to refer to internally refer to the jsFiles tag **/
  public static int jsFiles_ID = 1;

  /** any order, minOccurs=, type=ImportedJavaClassGroup **/
  public static String importedJavaClasses = "importedJavaClasses";
  /** id to refer to internally refer to the importedJavaClasses tag **/
  public static int importedJavaClasses_ID = 2;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript readObject;

    
  /** constructor **/
  public JavaScriptSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
     if (JavaScriptSAXReader.jsFiles.equals(tag)) {
       stackParser.addParser(new JsFileGroupSAXReader (stackParser,tag,tagAttributes,this,JavaScriptSAXReader.jsFiles_ID));
     }
    else
    

     if (JavaScriptSAXReader.importedJavaClasses.equals(tag)) {
       stackParser.addParser(new ImportedJavaClassGroupSAXReader (stackParser,tag,tagAttributes,this,JavaScriptSAXReader.importedJavaClasses_ID));
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
      
       case 1: //case JavaScriptSAXReader.jsFiles_ID
            if (result !=null) {
              readObject.setJsFiles((List)result);
            }
           
          break;    

       case 2: //case JavaScriptSAXReader.importedJavaClasses_ID
            if (result !=null) {
              readObject.setImportedJavaClasses((List)result);
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
 
