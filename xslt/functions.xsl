<?xml version="1.0" encoding="ISO-8859-1"?>



<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
xmlns:DMFn="http://www.StottlerHenke.com/datamontage"
>


<xsl:function name="DMFn:isCollection">
 <xsl:param name="modelName"/>

   <xsl:choose>



  <xsl:when test="$modelName = 'ParameterGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'ConstantGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'CategoryGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'DescriptorGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'PolyGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'IndexGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'LocalGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'ActionNodeGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'CompoundActionNodeGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'BindingGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'ConditionGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'StartGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'ConnectorGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'GlobalGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'JsFileGroup'">1</xsl:when>

  <xsl:when test="$modelName = 'ImportedJavaClassGroup'">1</xsl:when>



  <xsl:otherwise>0</xsl:otherwise>
 </xsl:choose>

</xsl:function>


<xsl:function name="DMFn:getCollectionTag">
 <xsl:param name="modelName"/>
 
   <xsl:choose>



  <xsl:when test="$modelName = 'ParameterGroup'">param</xsl:when>

  <xsl:when test="$modelName = 'ConstantGroup'">constant</xsl:when>

  <xsl:when test="$modelName = 'CategoryGroup'">category</xsl:when>

  <xsl:when test="$modelName = 'DescriptorGroup'">descrptor</xsl:when>

  <xsl:when test="$modelName = 'PolyGroup'">poly</xsl:when>

  <xsl:when test="$modelName = 'IndexGroup'">index</xsl:when>

  <xsl:when test="$modelName = 'LocalGroup'">local</xsl:when>

  <xsl:when test="$modelName = 'ActionNodeGroup'">actionNode</xsl:when>

  <xsl:when test="$modelName = 'CompoundActionNodeGroup'">compoundActionNode</xsl:when>

  <xsl:when test="$modelName = 'BindingGroup'">binding</xsl:when>

  <xsl:when test="$modelName = 'ConditionGroup'">condition</xsl:when>

  <xsl:when test="$modelName = 'StartGroup'">start</xsl:when>

  <xsl:when test="$modelName = 'ConnectorGroup'">connector</xsl:when>

  <xsl:when test="$modelName = 'GlobalGroup'">global</xsl:when>

  <xsl:when test="$modelName = 'JsFileGroup'">jsFile</xsl:when>

  <xsl:when test="$modelName = 'ImportedJavaClassGroup'">importedJavaClass</xsl:when>


  <xsl:otherwise>"error"</xsl:otherwise>
 </xsl:choose>

</xsl:function>   
</xsl:stylesheet>

