

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:all /> 
   &lt;xsd:element name="author" type="xsd:string" /> 
   &lt;xsd:element name="projectName" type="xsd:string" /> 
   &lt;xsd:element name="description" type="xsd:string" /> 
   &lt;xsd:element name="dateLastUpdate" type="xsd:string" /> 
   &lt;xsd:element name="simbionicVersion" type="xsd:string" /> 
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
    

public class ProjectPropertiesSAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.ProjectProperties dmObject, PrintWriter writer, int indent) {
   
     Utils.writeField(ProjectPropertiesSAXReader.author,dmObject.getAuthor(),writer,indent+1);
     
     Utils.writeField(ProjectPropertiesSAXReader.projectName,dmObject.getProjectName(),writer,indent+1);
     
     Utils.writeField(ProjectPropertiesSAXReader.description,dmObject.getDescription(),writer,indent+1);
     
     Utils.writeField(ProjectPropertiesSAXReader.dateLastUpdate,dmObject.getDateLastUpdate(),writer,indent+1);
     
     Utils.writeField(ProjectPropertiesSAXReader.simbionicVersion,dmObject.getSimbionicVersion(),writer,indent+1);
     
  }




 } 
 
