

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:all /> 
   &lt;xsd:element name="id" type="xsd:integer" /> 
   &lt;xsd:element name="expr" type="xsd:string" /> 
   &lt;xsd:element name="cx" type="xsd:integer" /> 
   &lt;xsd:element name="cy" type="xsd:integer" /> 
   &lt;xsd:element name="width" type="xsd:integer" /> 
   &lt;xsd:element name="height" type="xsd:integer" /> 
   &lt;xsd:element name="comment" type="xsd:string" /> 
   &lt;xsd:element name="labelMode" type="xsd:integer" /> 
   &lt;xsd:element name="isFinal" type="xsd:boolean" /> 
   &lt;xsd:element name="isBehavior" type="xsd:boolean" /> 
   &lt;xsd:element name="bindings" type="BindingGroup" /> 
   &lt;xsd:element name="isAlways" type="xsd:boolean" /> 
   &lt;xsd:element name="isCatch" type="xsd:boolean" /> 
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
    

public class ActionNodeSAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode dmObject, PrintWriter writer, int indent) {
   
    Utils.writeField(ActionNodeSAXReader.id,dmObject.getId(),writer,indent+1);
     
     Utils.writeField(ActionNodeSAXReader.expr,dmObject.getExpr(),writer,indent+1);
     
    Utils.writeField(ActionNodeSAXReader.cx,dmObject.getCx(),writer,indent+1);
     
    Utils.writeField(ActionNodeSAXReader.cy,dmObject.getCy(),writer,indent+1);
     
    Utils.writeField(ActionNodeSAXReader.width,dmObject.getWidth(),writer,indent+1);
     
    Utils.writeField(ActionNodeSAXReader.height,dmObject.getHeight(),writer,indent+1);
     
     Utils.writeField(ActionNodeSAXReader.comment,dmObject.getComment(),writer,indent+1);
     
    Utils.writeField(ActionNodeSAXReader.labelMode,dmObject.getLabelMode(),writer,indent+1);
     
     Utils.writeField(ActionNodeSAXReader.isFinal,dmObject.isFinal(),writer,indent+1);
     
     Utils.writeField(ActionNodeSAXReader.isBehavior,dmObject.isBehavior(),writer,indent+1);
     List<com.stottlerhenke.simbionic.common.xmlConverters.model.Binding>  bindings = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Binding>)dmObject.getBindings();
      if (bindings != null && !bindings.isEmpty()) {
        Utils.writeStartTag(ActionNodeSAXReader.bindings,writer,indent+1);
        BindingGroupSAXWriter.write(bindings, writer,indent+2);
        Utils.writeEndTag(ActionNodeSAXReader.bindings,writer,indent+1);
      }
      
     Utils.writeField(ActionNodeSAXReader.isAlways,dmObject.isAlways(),writer,indent+1);
     
     Utils.writeField(ActionNodeSAXReader.isCatch,dmObject.isCatch(),writer,indent+1);
     
  }




 } 
 
