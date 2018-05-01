

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:choice minOccurs="0" maxOccurs="unbounded" /> 
   &lt;xsd:element name="action" type="Action" /> 
   &lt;xsd:element name="actionFolder" type="ActionFolder" /> 
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
    

public class ActionFolderGroupSAXWriter  {

  // javaCollectionType(ActionFolderGroupSAXWriter)=List

 /** 
  * Write a collection of object of type 
  **/
  
  
 public static void write (ActionFolderGroup dmObjects, PrintWriter writer, int indent) {
    if (dmObjects == null) return;
	
    for (Object obj : dmObjects.getActionOrActionFolder()) {
 
      if (obj == null) continue; 
       
         
      if (obj instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.Action) {
         Utils.writeStartTag(ActionFolderGroupSAXReader.action,writer,indent+1);
         ActionSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.Action)obj,writer,indent+2);
         Utils.writeEndTag(ActionFolderGroupSAXReader.action,writer,indent+1);
         continue;
      }
         
         
      if (obj instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolder) {
         Utils.writeStartTag(ActionFolderGroupSAXReader.actionFolder,writer,indent+1);
         ActionFolderSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolder)obj,writer,indent+2);
         Utils.writeEndTag(ActionFolderGroupSAXReader.actionFolder,writer,indent+1);
         continue;
      }
           
    } 
 }




 } 
 
