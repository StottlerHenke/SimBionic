

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:all /> 
   &lt;xsd:element name="id" type="xsd:integer" /> 
   &lt;xsd:element name="endId" type="xsd:integer" /> 
   &lt;xsd:element name="endType" type="xsd:integer" /> 
   &lt;xsd:element name="startX" type="xsd:integer" /> 
   &lt;xsd:element name="startY" type="xsd:integer" /> 
   &lt;xsd:element name="endX" type="xsd:integer" /> 
   &lt;xsd:element name="endY" type="xsd:integer" /> 
   &lt;xsd:element name="priority" type="xsd:integer" /> 
   &lt;xsd:element name="interrupt" type="xsd:boolean" /> 
   &lt;xsd:element name="comment" type="xsd:string" /> 
   &lt;xsd:element name="labelMode" type="xsd:integer" /> 
   &lt;xsd:element name="bindings" type="BindingGroup" /> 
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
    

public class ConnectorSAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.Connector dmObject, PrintWriter writer, int indent) {
   
    Utils.writeField(ConnectorSAXReader.id,dmObject.getId(),writer,indent+1);
     
    Utils.writeField(ConnectorSAXReader.endId,dmObject.getEndId(),writer,indent+1);
     
    Utils.writeField(ConnectorSAXReader.endType,dmObject.getEndType(),writer,indent+1);
     
    Utils.writeField(ConnectorSAXReader.startX,dmObject.getStartX(),writer,indent+1);
     
    Utils.writeField(ConnectorSAXReader.startY,dmObject.getStartY(),writer,indent+1);
     
    Utils.writeField(ConnectorSAXReader.endX,dmObject.getEndX(),writer,indent+1);
     
    Utils.writeField(ConnectorSAXReader.endY,dmObject.getEndY(),writer,indent+1);
     
    Utils.writeField(ConnectorSAXReader.priority,dmObject.getPriority(),writer,indent+1);
     
     Utils.writeField(ConnectorSAXReader.interrupt,dmObject.isInterrupt(),writer,indent+1);
     
     Utils.writeField(ConnectorSAXReader.comment,dmObject.getComment(),writer,indent+1);
     
    Utils.writeField(ConnectorSAXReader.labelMode,dmObject.getLabelMode(),writer,indent+1);
     List<com.stottlerhenke.simbionic.common.xmlConverters.model.Binding>  bindings = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Binding>)dmObject.getBindings();
      if (bindings != null && !bindings.isEmpty()) {
        Utils.writeStartTag(ConnectorSAXReader.bindings,writer,indent+1);
        BindingGroupSAXWriter.write(bindings, writer,indent+2);
        Utils.writeEndTag(ConnectorSAXReader.bindings,writer,indent+1);
      }
      
  }




 } 
 
