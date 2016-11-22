

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
    

public class ConditionSAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.Condition dmObject, PrintWriter writer, int indent) {
   
    Utils.writeField(ConditionSAXReader.id,dmObject.getId(),writer,indent+1);
     
     Utils.writeField(ConditionSAXReader.expr,dmObject.getExpr(),writer,indent+1);
     
    Utils.writeField(ConditionSAXReader.cx,dmObject.getCx(),writer,indent+1);
     
    Utils.writeField(ConditionSAXReader.cy,dmObject.getCy(),writer,indent+1);
     
     Utils.writeField(ConditionSAXReader.comment,dmObject.getComment(),writer,indent+1);
     
    Utils.writeField(ConditionSAXReader.labelMode,dmObject.getLabelMode(),writer,indent+1);
     List<com.stottlerhenke.simbionic.common.xmlConverters.model.Binding>  bindings = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Binding>)dmObject.getBindings();
      if (bindings != null && !bindings.isEmpty()) {
        Utils.writeStartTag(ConditionSAXReader.bindings,writer,indent+1);
        BindingGroupSAXWriter.write(bindings, writer,indent+2);
        Utils.writeEndTag(ConditionSAXReader.bindings,writer,indent+1);
      }
      
  }




 } 
 
