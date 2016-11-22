

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:all /> 
   &lt;xsd:element name="name" type="xsd:string" /> 
   &lt;xsd:element name="description" type="xsd:string" /> 
   &lt;xsd:element name="exec" type="xsd:integer" /> 
   &lt;xsd:element name="interrupt" type="xsd:boolean" /> 
   &lt;xsd:element name="parameters" type="ParameterGroup" minOccurs="0" /> 
   &lt;xsd:element name="polys" type="PolyGroup" /> 
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
    

public class BehaviorSAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior dmObject, PrintWriter writer, int indent) {
   
     Utils.writeField(BehaviorSAXReader.name,dmObject.getName(),writer,indent+1);
     
     Utils.writeField(BehaviorSAXReader.description,dmObject.getDescription(),writer,indent+1);
     
    Utils.writeField(BehaviorSAXReader.exec,dmObject.getExec(),writer,indent+1);
     
     Utils.writeField(BehaviorSAXReader.interrupt,dmObject.isInterrupt(),writer,indent+1);
     List<com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter>  parameters = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter>)dmObject.getParameters();
      if (parameters != null && !parameters.isEmpty()) {
        Utils.writeStartTag(BehaviorSAXReader.parameters,writer,indent+1);
        ParameterGroupSAXWriter.write(parameters, writer,indent+2);
        Utils.writeEndTag(BehaviorSAXReader.parameters,writer,indent+1);
      }
      List<com.stottlerhenke.simbionic.common.xmlConverters.model.Poly>  polys = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Poly>)dmObject.getPolys();
      if (polys != null && !polys.isEmpty()) {
        Utils.writeStartTag(BehaviorSAXReader.polys,writer,indent+1);
        PolyGroupSAXWriter.write(polys, writer,indent+2);
        Utils.writeEndTag(BehaviorSAXReader.polys,writer,indent+1);
      }
      
  }




 } 
 
