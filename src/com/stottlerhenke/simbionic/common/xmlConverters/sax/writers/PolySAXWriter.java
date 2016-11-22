

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:all /> 
   &lt;xsd:element name="indices" type="IndexGroup" /> 
   &lt;xsd:element name="locals" type="LocalGroup" /> 
   &lt;xsd:element name="nodes" type="NodeGroup" /> 
   &lt;xsd:element name="conditions" type="ConditionGroup" /> 
   &lt;xsd:element name="connectors" type="StartConnectorGroup" /> 
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
    

public class PolySAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.Poly dmObject, PrintWriter writer, int indent) {
   List<String>  indices = (List<String>)dmObject.getIndices();
      if (indices != null && !indices.isEmpty()) {
        Utils.writeStartTag(PolySAXReader.indices,writer,indent+1);
        IndexGroupSAXWriter.write(indices, writer,indent+2);
        Utils.writeEndTag(PolySAXReader.indices,writer,indent+1);
      }
      List<com.stottlerhenke.simbionic.common.xmlConverters.model.Local>  locals = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Local>)dmObject.getLocals();
      if (locals != null && !locals.isEmpty()) {
        Utils.writeStartTag(PolySAXReader.locals,writer,indent+1);
        LocalGroupSAXWriter.write(locals, writer,indent+2);
        Utils.writeEndTag(PolySAXReader.locals,writer,indent+1);
      }
      com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup nodes = (com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup)dmObject.getNodes();
      if (nodes != null) {
        Utils.writeStartTag(PolySAXReader.nodes,writer,indent+1);
        NodeGroupSAXWriter.write(nodes,writer,indent+2);
        Utils.writeEndTag(PolySAXReader.nodes,writer,indent+1);
      }
      List<com.stottlerhenke.simbionic.common.xmlConverters.model.Condition>  conditions = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Condition>)dmObject.getConditions();
      if (conditions != null && !conditions.isEmpty()) {
        Utils.writeStartTag(PolySAXReader.conditions,writer,indent+1);
        ConditionGroupSAXWriter.write(conditions, writer,indent+2);
        Utils.writeEndTag(PolySAXReader.conditions,writer,indent+1);
      }
      List<com.stottlerhenke.simbionic.common.xmlConverters.model.Start>  connectors = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Start>)dmObject.getConnectors();
      if (connectors != null && !connectors.isEmpty()) {
        Utils.writeStartTag(PolySAXReader.connectors,writer,indent+1);
        StartConnectorGroupSAXWriter.write(connectors, writer,indent+2);
        Utils.writeEndTag(PolySAXReader.connectors,writer,indent+1);
      }
      
  }




 } 
 
