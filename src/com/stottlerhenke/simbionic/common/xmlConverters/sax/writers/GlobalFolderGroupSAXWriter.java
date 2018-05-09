

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:choice minOccurs="0" maxOccurs="unbounded" /> 
   &lt;xsd:element name="global" type="Global" /> 
   &lt;xsd:element name="globalFolder" type="GlobalFolder" /> 
  &lt;/xsd:choice> 
   </pre>
*/


package com.stottlerhenke.simbionic.common.xmlConverters.sax.writers;
import com.stottlerhenke.simbionic.common.xmlConverters.model.*;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.StackParser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.readers.*;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.writers.Utils;

import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
    

public class GlobalFolderGroupSAXWriter  {

  // javaCollectionType(GlobalFolderGroupSAXWriter)=List

 /** 
  * Write a collection of object of type 
  **/
  
  
 public static void write (GlobalFolderGroup dmObjects, PrintWriter writer, int indent) {
    if (dmObjects == null) return;
	
    for (Object obj : dmObjects.getGlobalOrGlobalFolder()) {
 
      if (obj == null) continue; 
       
         
      if (obj instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.Global) {
         Utils.writeStartTag(GlobalFolderGroupSAXReader.global,writer,indent+1);
         GlobalSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.Global)obj,writer,indent+2);
         Utils.writeEndTag(GlobalFolderGroupSAXReader.global,writer,indent+1);
         continue;
      }
         
         
      if (obj instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolder) {
         Utils.writeStartTag(GlobalFolderGroupSAXReader.globalFolder,writer,indent+1);
         GlobalFolderSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolder)obj,writer,indent+2);
         Utils.writeEndTag(GlobalFolderGroupSAXReader.globalFolder,writer,indent+1);
         continue;
      }
           
    } 
 }




 } 
 
