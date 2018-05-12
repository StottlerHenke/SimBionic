

/*
 * Class automatically generated using XSLT translator
 * See Taskguide/xslt/Readme.doc describing how to run tthe XSLT translator and
 * an explanation of the generated code.
 *
 
 <pre> 
   &lt;xsd:all /> 
   &lt;xsd:element name="version" type="xsd:integer" /> 
   &lt;xsd:element name="ipAddress" type="xsd:string" /> 
   &lt;xsd:element name="loopBack" type="xsd:boolean" /> 
   &lt;xsd:element name="main" type="xsd:string" /> 
   &lt;xsd:element name="actions" type="ActionFolderGroup" /> 
   &lt;xsd:element name="predicates" type="PredicateFolderGroup" /> 
   &lt;xsd:element name="constants" type="ConstantFolderGroup" /> 
   &lt;xsd:element name="categories" type="CategoryGroup" /> 
   &lt;xsd:element name="behaviors" type="BehaviorFolderGroup" /> 
   &lt;xsd:element name="globals" type="GlobalFolderGroup" /> 
   &lt;xsd:element name="javaScript" type="JavaScript" /> 
   &lt;xsd:element name="projectProperties" type="ProjectProperties" /> 
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
    

public class SimBionicJavaSAXWriter  {
 
  
  /** 
   * write the given taskguide object to the given xml file
   * 
   * @param dmObject -- object to be writen
   * @param writer -- xml output file
   * @param indent -- indent used to generate the xml tags associated with the input object
  **/
  
  public static void write (com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava dmObject, PrintWriter writer, int indent) {
   
    Utils.writeField(SimBionicJavaSAXReader.version,dmObject.getVersion(),writer,indent+1);
     
     Utils.writeField(SimBionicJavaSAXReader.ipAddress,dmObject.getIpAddress(),writer,indent+1);
     
     Utils.writeField(SimBionicJavaSAXReader.loopBack,dmObject.isLoopBack(),writer,indent+1);
     
     Utils.writeField(SimBionicJavaSAXReader.main,dmObject.getMain(),writer,indent+1);
     com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup actions = (com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup)dmObject.getActions();
      if (actions != null) {
        Utils.writeStartTag(SimBionicJavaSAXReader.actions,writer,indent+1);
        ActionFolderGroupSAXWriter.write(actions,writer,indent+2);
        Utils.writeEndTag(SimBionicJavaSAXReader.actions,writer,indent+1);
      }
      com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup predicates = (com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup)dmObject.getPredicates();
      if (predicates != null) {
        Utils.writeStartTag(SimBionicJavaSAXReader.predicates,writer,indent+1);
        PredicateFolderGroupSAXWriter.write(predicates,writer,indent+2);
        Utils.writeEndTag(SimBionicJavaSAXReader.predicates,writer,indent+1);
      }
      com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolderGroup constants = (com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolderGroup)dmObject.getConstants();
      if (constants != null) {
        Utils.writeStartTag(SimBionicJavaSAXReader.constants,writer,indent+1);
        ConstantFolderGroupSAXWriter.write(constants,writer,indent+2);
        Utils.writeEndTag(SimBionicJavaSAXReader.constants,writer,indent+1);
      }
      List<com.stottlerhenke.simbionic.common.xmlConverters.model.Category>  categories = (List<com.stottlerhenke.simbionic.common.xmlConverters.model.Category>)dmObject.getCategories();
      if (categories != null && !categories.isEmpty()) {
        Utils.writeStartTag(SimBionicJavaSAXReader.categories,writer,indent+1);
        CategoryGroupSAXWriter.write(categories, writer,indent+2);
        Utils.writeEndTag(SimBionicJavaSAXReader.categories,writer,indent+1);
      }
      com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup behaviors = (com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup)dmObject.getBehaviors();
      if (behaviors != null) {
        Utils.writeStartTag(SimBionicJavaSAXReader.behaviors,writer,indent+1);
        BehaviorFolderGroupSAXWriter.write(behaviors,writer,indent+2);
        Utils.writeEndTag(SimBionicJavaSAXReader.behaviors,writer,indent+1);
      }
      com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolderGroup globals = (com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolderGroup)dmObject.getGlobals();
      if (globals != null) {
        Utils.writeStartTag(SimBionicJavaSAXReader.globals,writer,indent+1);
        GlobalFolderGroupSAXWriter.write(globals,writer,indent+2);
        Utils.writeEndTag(SimBionicJavaSAXReader.globals,writer,indent+1);
      }
      com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript javaScript = (com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript)dmObject.getJavaScript();
      if (javaScript != null) {
        Utils.writeStartTag(SimBionicJavaSAXReader.javaScript,writer,indent+1);
        JavaScriptSAXWriter.write(javaScript,writer,indent+2);
        Utils.writeEndTag(SimBionicJavaSAXReader.javaScript,writer,indent+1);
      }
      com.stottlerhenke.simbionic.common.xmlConverters.model.ProjectProperties projectProperties = (com.stottlerhenke.simbionic.common.xmlConverters.model.ProjectProperties)dmObject.getProjectProperties();
      if (projectProperties != null) {
        Utils.writeStartTag(SimBionicJavaSAXReader.projectProperties,writer,indent+1);
        ProjectPropertiesSAXWriter.write(projectProperties,writer,indent+2);
        Utils.writeEndTag(SimBionicJavaSAXReader.projectProperties,writer,indent+1);
      }
      
  }




 } 
 
