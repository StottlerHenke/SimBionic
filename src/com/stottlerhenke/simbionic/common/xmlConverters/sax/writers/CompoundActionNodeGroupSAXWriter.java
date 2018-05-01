

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:sequence /> 
   &lt;xsd:element name="compoundActionNode" type="CompoundActionNode" minOccurs="0" maxOccurs="unbounded" /> 
  &lt;/xsd:sequence> 
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
    

public class CompoundActionNodeGroupSAXWriter  {

 /** 
  * Writes an array of TG objects to xml 
  *
 **/
 public static void write (List<com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode> dmObjects, PrintWriter writer, int indent) {
    if (dmObjects == null) return; 
    for (Iterator it = dmObjects.iterator(); it.hasNext(); ) {
      com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode dmChild = (com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode)it.next();
	  Utils.writeStartTag("compoundActionNode",writer,indent+1);
      CompoundActionNodeSAXWriter.write(dmChild,writer,indent+2);
      Utils.writeEndTag("compoundActionNode",writer,indent+1);
		
    }
 }




 } 
 
