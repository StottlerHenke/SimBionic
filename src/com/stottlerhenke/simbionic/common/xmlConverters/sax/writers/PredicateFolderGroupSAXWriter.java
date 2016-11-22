

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:choice minOccurs="0" maxOccurs="unbounded" /> 
   &lt;xsd:element name="predicate" type="Predicate" /> 
   &lt;xsd:element name="predicateFolder" type="PredicateFolder" /> 
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
    

public class PredicateFolderGroupSAXWriter  {

  // javaCollectionType(PredicateFolderGroupSAXWriter)=List
 /** 
  * Write a collection of object of type 
  **/
 public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup dmObjects, PrintWriter writer, int indent) {
    if (dmObjects == null) return;

    for (Object predicateOrPredicateFolder : dmObjects.getPredicateOrPredicateFolder()) {
      if (predicateOrPredicateFolder == null) continue; 
       
         
      if (predicateOrPredicateFolder instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate) {
         Utils.writeStartTag(PredicateFolderGroupSAXReader.predicate,writer,indent+1);
         PredicateSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate)predicateOrPredicateFolder,writer,indent+2);
         Utils.writeEndTag(PredicateFolderGroupSAXReader.predicate,writer,indent+1);
         continue;
      }
         
         
      if (predicateOrPredicateFolder instanceof com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolder) {
         Utils.writeStartTag(PredicateFolderGroupSAXReader.predicateFolder,writer,indent+1);
         PredicateFolderSAXWriter.write((com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolder)predicateOrPredicateFolder,writer,indent+2);
         Utils.writeEndTag(PredicateFolderGroupSAXReader.predicateFolder,writer,indent+1);
         continue;
      }
           
    } 
 }




 } 
 
