<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
xmlns:DMFn="http://www.StottlerHenke.com/datamontage"
>

<!--
	Given a className representing a DM class,
	return the qualified name (com.stottlerhenke.yourproject.datamontage.className)
	or a rename for the class.  NOTE: when customizing SXBT for your project, you
	will need to change the value in the concat() occurrence below to refer to
	your model's package.
-->
<xsl:function name="DMFn:getDMClass">
 <xsl:param name="className"/>
   <xsl:variable name="DMClassName" select="concat('com.stottlerhenke.simbionic.common.xmlConverters.model.',$className)" />
  <xsl:choose>
  <xsl:when test="DMFn:isBasicType($className)=1"><xsl:value-of select="DMFn:getBasicTypeJavaClass($className)"/></xsl:when>
  <xsl:when test="$className = ''">String</xsl:when>
  <xsl:when test="$className = 'ActionFolderGroup'">com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup</xsl:when>
  <xsl:when test="$className = 'ActionFolder'">com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolder</xsl:when>
  <xsl:when test="$className = 'ActionGroup'">com.stottlerhenke.simbionic.common.xmlConverters.model.ActionGroup</xsl:when>
  <xsl:when test="$className = 'Action'">com.stottlerhenke.simbionic.common.xmlConverters.model.Action</xsl:when>
  <xsl:when test="$className = 'PredicateFolder'">com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolder</xsl:when>
  <xsl:when test="$className = 'Predicate'">com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate</xsl:when>
  <xsl:when test="$className = 'Parameter'">com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter</xsl:when>
  <xsl:when test="$className = 'Constant'">com.stottlerhenke.simbionic.common.xmlConverters.model.Constant</xsl:when>
  <xsl:when test="$className = 'Descriptor'">com.stottlerhenke.simbionic.common.xmlConverters.model.Descriptor</xsl:when>
  <xsl:when test="$className = 'BehaviorFolder'">com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolder</xsl:when>
  <xsl:when test="$className = 'Behavior'">com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior</xsl:when>
  <xsl:when test="$className = 'Poly'">com.stottlerhenke.simbionic.common.xmlConverters.model.Poly</xsl:when>
  <xsl:when test="$className = 'Local'">com.stottlerhenke.simbionic.common.xmlConverters.model.Local</xsl:when>
  <xsl:when test="$className = 'ActionNode'">com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode</xsl:when>
  <xsl:when test="$className = 'CompoundActionNode'">com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode</xsl:when>
  <xsl:when test="$className = 'Binding'">com.stottlerhenke.simbionic.common.xmlConverters.model.Binding</xsl:when>
  <xsl:when test="$className = 'Condition'">com.stottlerhenke.simbionic.common.xmlConverters.model.Condition</xsl:when>
  <xsl:when test="$className = 'Start'">com.stottlerhenke.simbionic.common.xmlConverters.model.Start</xsl:when>
  <xsl:when test="$className = 'Connector'">com.stottlerhenke.simbionic.common.xmlConverters.model.Connector</xsl:when>
  <xsl:when test="$className = 'Global'">com.stottlerhenke.simbionic.common.xmlConverters.model.Global</xsl:when>
  <xsl:when test="$className = 'JavaScript'">com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript</xsl:when>
  <xsl:when test="$className = 'JsFile'">String</xsl:when>
  <xsl:when test="$className = 'ImportedJavaClass'">String</xsl:when>
   <xsl:when test="$className = 'Index'">String</xsl:when>
  <xsl:otherwise><xsl:value-of select="$DMClassName"/></xsl:otherwise>
 </xsl:choose>
</xsl:function>

<!--
  This function is used to specify a custom setter for
  an object field.  Given a className and a *candidate* name for a 
  setter, this function will return the *actual* name of the
  setter.  The *candidate* is the name returned by SXBT convention.
  In the example below, the setter generated for field 'id' is
  'setId()'.  However, if for whatever reason you need the
  setter to be 'setID()' (all caps), this function allows you
  to specify this.  
-->
<xsl:function name="DMFn:getDMSetterName">
 <xsl:param name="className"/>
 <xsl:param name="setter"/>
 <xsl:choose>
  	  <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode' and $setter = 'setIsAlways'">setAlways</xsl:when>
  	  <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode' and $setter = 'setIsCatch'">setCatch</xsl:when>
	  <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode' and $setter = 'setIsAlways'">setAlways</xsl:when>
  	  <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode' and $setter = 'setIsCatch'">setCatch</xsl:when>
  	  <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup' and $setter = 'setCompoundActionNode'">setCompoundActionNodes</xsl:when>
  	  <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Descriptor' and $setter = 'setDescrptorChildren'">setDescriptors</xsl:when>

	  <xsl:otherwise><xsl:value-of select="$setter"/></xsl:otherwise>
 </xsl:choose>
</xsl:function>   

<!--
  This function is used to specify a custom getter for
  an object field.  Given a className and a *candidate* name for a 
  getter, this function will return the *actual* name of the
  getter.  The *candidate* is the name returned by SXBT convention.
  In the example below, the getter generated for field 'id' is
  'getId()'.  However, if for whatever reason you need the
  setter to be 'setID()' (all caps), this function allows you
  to specify this name instead.
-->
<xsl:function name="DMFn:getDMGetterName">
 <xsl:param name="className"/>
 <xsl:param name="getter"/>
 <xsl:choose>
    <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode' and $getter = 'getIsFinal'">isFinal</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode' and $getter = 'getIsBehavior'">isBehavior</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode' and $getter = 'getIsAlways'">isAlways</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode' and $getter = 'getIsCatch'">isCatch</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Action' and $getter = 'getCore'">isCore</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate' and $getter = 'getCore'">isCore</xsl:when>
    <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode' and $getter = 'getIsFinal'">isFinal</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior' and $getter = 'getInterrupt'">isInterrupt</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Category' and $getter = 'getSelected'">isSelected</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Descriptor' and $getter = 'getSelected'">isSelected</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Connector' and $getter = 'getInterrupt'">isInterrupt</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Global' and $getter = 'getPolymorphic'">isPolymorphic</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava' and $getter = 'getLoopBack'">isLoopBack</xsl:when>
	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode' and $getter = 'getIsAlways'">isAlways</xsl:when>
  	<xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode' and $getter = 'getIsCatch'">isCatch</xsl:when>
    <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup' and $getter = 'getCompoundActionNode'">getCompoundActionNodes</xsl:when>
    <xsl:when test="$className = 'com.stottlerhenke.simbionic.common.xmlConverters.model.Descriptor' and $getter = 'getDescrptorChildren'">getDescriptors</xsl:when> 
 <xsl:otherwise><xsl:value-of select="$getter"/></xsl:otherwise>
 </xsl:choose>
</xsl:function>  

<!--
	return 1 if a given tag should not be written.
	This is the case when for backward compatibility a tag has been maintained
	but it is not longer used
-->
<xsl:function name="DMFn:doNotWriteField">
 <xsl:param name="className"/>
 <xsl:param name="tag"/>
  0
</xsl:function>  

<!--
	Some fields are declared as "strings" in the schema but the getter returns an object.
	Cast the value of the getter to an string when appropriate.
-->
<xsl:function name="DMFn:getWriteFieldCast">
 <xsl:param name="className"/>
 <xsl:param name="field"/>
   <xsl:choose>
   <xsl:when test="$className = 'com.StottlerHenke.taskguide.engine.BooleanVariable' and $field = 'initialValue'">(Boolean)</xsl:when>
   <xsl:when test="$className = 'com.StottlerHenke.taskguide.engine.DoubleVariable' and $field = 'initialValue'">(Double)</xsl:when>
   <xsl:when test="$className = 'com.StottlerHenke.taskguide.engine.IntegerVariable' and $field = 'initialValue'">(Integer)</xsl:when>      	  
   <xsl:when test="$className = 'com.StottlerHenke.taskguide.engine.StringVariable' and $field = 'initialValue'">(String)</xsl:when> 
  <xsl:otherwise></xsl:otherwise><!-- no cast needed -->
 </xsl:choose>
</xsl:function> 

<!--
  The default implementation of collection is List.
  But some classes used List or Vectors.
  This function captures those exceptions	
 -->
<xsl:function name="DMFn:getJavaCollectionType">
 <xsl:param name="className"/>
 <xsl:param name="getter"/>
   <xsl:choose>
   <xsl:when test="$className = 'com.StottlerHenke.taskguide.engine.ClassSpecGroup' and $getter = 'getChildren'">List</xsl:when>
   
   <!-- Special cases for writers taking as input a collection of objects-->
   <xsl:when test="$className = 'ClassSpecListTypeSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'ClassMemberListTypeSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'VariableListTypeSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'CalculationGroupTypeSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'ConditionalBranchGroupSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'NoteGroupTypeSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'ProcedureRefGroupTypeSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'TaskNodeGroupSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'ListValueTypeSAXWriter'">Vector</xsl:when>
   <xsl:when test="$className = 'ParamListTypeSAXWriter'">List</xsl:when>
   <xsl:when test="$className = 'ChangeLogTypeSAXWriter'">Vector</xsl:when>
   <xsl:when test="$className = 'TableValueTypeSAXWriter'">Vector</xsl:when>
  <xsl:otherwise>List</xsl:otherwise>
 </xsl:choose>
</xsl:function>    	  

<xsl:function name="DMFn:isCompositeCollection">
 <xsl:param name="className"/>
 <xsl:choose>
   <xsl:when test="$className = 'ActionFolderGroup'">1</xsl:when>
   <xsl:when test="$className = 'BehaviorFolderGroup'">1</xsl:when>
   <xsl:when test="$className = 'PredicateFolderGroup'">1</xsl:when>
   <xsl:when test="$className = 'GlobalFolderGroup'">1</xsl:when>
   <xsl:when test="$className = 'ConstantFolderGroup'">1</xsl:when>
  <xsl:otherwise>0</xsl:otherwise>
 </xsl:choose>
</xsl:function>  
  	  
<xsl:function name="DMFn:getCompositeCollectionAccessor">
 <xsl:param name="className"/>
 <xsl:choose>
   <xsl:when test="$className = 'ActionFolderGroup'">getActionOrActionFolder()</xsl:when>
   <xsl:when test="$className = 'BehaviorFolderGroup'">getBehaviorOrBehaviorFolder()</xsl:when>
   <xsl:when test="$className = 'PredicateFolderGroup'">getPredicateOrPredicateFolder()</xsl:when>
   <xsl:when test="$className = 'GlobalFolderGroup'">getGlobalOrGlobalFolder()</xsl:when>
   <xsl:when test="$className = 'ConstantFolderGroup'">getConstantOrConstantFolder()</xsl:when>
  <xsl:otherwise>''</xsl:otherwise>
 </xsl:choose>
</xsl:function>  



<!--  Returns 1 (true)for Java primitives and the NullType, 0 (false)otherwise -->
<xsl:function name="DMFn:isBasicType">
 <xsl:param name="type"/>
   <xsl:choose>
   <xsl:when test="$type = 'xsd:string'">1</xsl:when>
   <xsl:when test="$type = 'xsd:boolean'">1</xsl:when>	
   <xsl:when test="$type = 'xsd:integer'">1</xsl:when>
   <xsl:when test="$type = 'xsd:double'">1</xsl:when>
   <xsl:when test="$type = 'NullType'">1</xsl:when>
   <xsl:otherwise>0</xsl:otherwise>
 </xsl:choose>
</xsl:function>

<xsl:function name="DMFn:getBasicTypeJavaClass">
 <xsl:param name="type"/>
   <xsl:choose>
   <xsl:when test="$type = 'xsd:string'">String</xsl:when>
   <xsl:when test="$type = 'xsd:boolean'">Boolean</xsl:when>	
   <xsl:when test="$type = 'xsd:integer'">Integer</xsl:when>
   <xsl:when test="$type = 'xsd:double'">Double</xsl:when>

   <xsl:otherwise>String</xsl:otherwise><!-- fixme -->
 </xsl:choose>
</xsl:function>

<!--

-->
<xsl:function name="DMFn:getCollectionType">
  <xsl:param name="complexType"/>
  <xsl:param name="elementType"/>
  <xsl:choose>
  <xsl:when test="$complexType = 'TableValueType' and $elementType='ListValueType'">Vector</xsl:when>
  <xsl:otherwise><xsl:value-of select="DMFn:getDMClass($elementType)"/></xsl:otherwise>
  </xsl:choose>
</xsl:function>
</xsl:stylesheet>

