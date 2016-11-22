

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:all /> 
   &lt;xsd:element name="jsFiles" type="JsFileGroup" /> 
   &lt;xsd:element name="importedJavaClasses" type="ImportedJavaClassGroup" /> 
  &lt;/xsd:all> 
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
    

public class JavaScriptSAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript dmObject, PrintWriter writer, int indent) {
   List<String>  jsFiles = (List<String>)dmObject.getJsFiles();
      if (jsFiles != null && !jsFiles.isEmpty()) {
        Utils.writeStartTag(JavaScriptSAXReader.jsFiles,writer,indent+1);
        JsFileGroupSAXWriter.write(jsFiles, writer,indent+2);
        Utils.writeEndTag(JavaScriptSAXReader.jsFiles,writer,indent+1);
      }
      List<String>  importedJavaClasses = (List<String>)dmObject.getImportedJavaClasses();
      if (importedJavaClasses != null && !importedJavaClasses.isEmpty()) {
        Utils.writeStartTag(JavaScriptSAXReader.importedJavaClasses,writer,indent+1);
        ImportedJavaClassGroupSAXWriter.write(importedJavaClasses, writer,indent+2);
        Utils.writeEndTag(JavaScriptSAXReader.importedJavaClasses,writer,indent+1);
      }
      
  }




 } 
 
