

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:choice minOccurs="0" maxOccurs="unbounded" /> 
   &lt;xsd:element name="behavior" type="Behavior" /> 
   &lt;xsd:element name="behaviorFolder" type="BehaviorFolder" /> 
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
    

public class BehaviorFolderGroupSAXWriter  {

  // javaCollectionType(BehaviorFolderGroupSAXWriter)=List
 /** 
  * Write a collection of object of type 
  **/
 public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup dmObjects, PrintWriter writer, int indent) {
    if (dmObjects == null) return;

    for (Object behaviorOrBehaviorFolder : dmObjects.getBehaviorOrBehaviorFolder()) {
      if (behaviorOrBehaviorFolder == null) continue; 
       
         
      if (behaviorOrBehaviorFolder instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior) {
         Utils.writeStartTag(BehaviorFolderGroupSAXReader.behavior,writer,indent+1);
         BehaviorSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior)behaviorOrBehaviorFolder,writer,indent+2);
         Utils.writeEndTag(BehaviorFolderGroupSAXReader.behavior,writer,indent+1);
         continue;
      }
         
         
      if (behaviorOrBehaviorFolder instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolder) {
         Utils.writeStartTag(BehaviorFolderGroupSAXReader.behaviorFolder,writer,indent+1);
         BehaviorFolderSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolder)behaviorOrBehaviorFolder,writer,indent+2);
         Utils.writeEndTag(BehaviorFolderGroupSAXReader.behaviorFolder,writer,indent+1);
         continue;
      }
           
    } 
 }




 } 
 
