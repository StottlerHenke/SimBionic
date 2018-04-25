

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:all /> 
   &lt;xsd:element name="initial" type="xsd:integer" /> 
   &lt;xsd:element name="actionNodes" type="ActionNodeGroup" /> 
   &lt;xsd:element name="compoundActionNode" type="CompoundActionNodeGroup" /> 
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
    

public class NodeGroupSAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup dmObject, PrintWriter writer, int indent) {
   
    Utils.writeField(NodeGroupSAXReader.initial,dmObject.getInitial(),writer,indent+1);
     List<com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode>  actionNodes = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode>)dmObject.getActionNodes();
      if (actionNodes != null && !actionNodes.isEmpty()) {
        Utils.writeStartTag(NodeGroupSAXReader.actionNodes,writer,indent+1);
        ActionNodeGroupSAXWriter.write(actionNodes, writer,indent+2);
        Utils.writeEndTag(NodeGroupSAXReader.actionNodes,writer,indent+1);
      }
      List<com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode>  compoundActionNode = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode>)dmObject.getCompoundActionNodes();
      if (compoundActionNode != null && !compoundActionNode.isEmpty()) {
        Utils.writeStartTag(NodeGroupSAXReader.compoundActionNode,writer,indent+1);
        CompoundActionNodeGroupSAXWriter.write(compoundActionNode, writer,indent+2);
        Utils.writeEndTag(NodeGroupSAXReader.compoundActionNode,writer,indent+1);
      }
      
  }




 } 
 
