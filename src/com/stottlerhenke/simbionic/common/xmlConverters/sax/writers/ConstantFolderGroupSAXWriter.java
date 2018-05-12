

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:choice minOccurs="0" maxOccurs="unbounded" /> 
   &lt;xsd:element name="constant" type="Constant" /> 
   &lt;xsd:element name="constantFolder" type="ConstantFolder" /> 
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
    

public class ConstantFolderGroupSAXWriter  {

  // javaCollectionType(ConstantFolderGroupSAXWriter)=List

 /** 
  * Write a collection of object of type 
  **/
  
  
 public static void write (ConstantFolderGroup dmObjects, PrintWriter writer, int indent) {
    if (dmObjects == null) return;
	
    for (Object obj : dmObjects.getConstantOrConstantFolder()) {
 
      if (obj == null) continue; 
       
         
      if (obj instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.Constant) {
         Utils.writeStartTag(ConstantFolderGroupSAXReader.constant,writer,indent+1);
         ConstantSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.Constant)obj,writer,indent+2);
         Utils.writeEndTag(ConstantFolderGroupSAXReader.constant,writer,indent+1);
         continue;
      }
         
         
      if (obj instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolder) {
         Utils.writeStartTag(ConstantFolderGroupSAXReader.constantFolder,writer,indent+1);
         ConstantFolderSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolder)obj,writer,indent+2);
         Utils.writeEndTag(ConstantFolderGroupSAXReader.constantFolder,writer,indent+1);
         continue;
      }
           
    } 
 }




 } 
 
