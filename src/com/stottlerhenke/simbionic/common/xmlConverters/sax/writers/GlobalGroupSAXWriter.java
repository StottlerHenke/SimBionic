

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:sequence /> 
   &lt;xsd:element name="global" type="Global" minOccurs="0" maxOccurs="unbounded" /> 
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
    

public class GlobalGroupSAXWriter  {

 /** 
  * Writes an array of TG objects to xml 
  *
 **/
 public static void write (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Global> dmObjects, PrintWriter writer, int indent) {
    if (dmObjects == null) return; 
    for (Iterator it = dmObjects.iterator(); it.hasNext(); ) {
      com.stottlerhenke.simbionic.common.xmlConverters.model.Global dmChild = (com.stottlerhenke.simbionic.common.xmlConverters.model.Global)it.next();
      Utils.writeStartTag("global",writer,indent+1);
      GlobalSAXWriter.write(dmChild,writer,indent+2);
      Utils.writeEndTag("global",writer,indent+1);
    }
 }




 } 
 
