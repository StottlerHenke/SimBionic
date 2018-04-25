<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Reads the Tasguide schema and generates an xml file 
	(collections.xml) indicating which types in the schema 
	represent a collection of objects.
-->
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
>

<xsl:output method="text" />
<xsl:output method="html" indent="yes" name="html" />

<xsl:template match="/">

<!--
	Any schema type that that can have more that one object
	is a collection 
-->
<xsl:for-each select="xsd:schema/xsd:complexType">
  <xsl:if test="count(xsd:sequence) > 0">
     &lt;collection&gt; 
      &lt;name&gt;<xsl:value-of select="@name"/>&lt;/name&gt;
     &lt;tag&gt;<xsl:value-of select="xsd:sequence/xsd:element/@name"/>&lt;/tag&gt;
     &lt;/collection&gt;
    
  </xsl:if>

</xsl:for-each>

<!--
<xsl:for-each select="xsd:schema/xsd:complexType/xsd:choice">
  <xsl:choose>
  <xsl:when test="@maxOccurs = 'unbounded'">
     &lt;collection&gt;
      &lt;name&gt; <xsl:value-of select="../@name"/>&lt;/name&gt; 
      &lt;/collection&gt;
    
  </xsl:when>
  <xsl:when test="@maxOccurs >= 2">
     &lt;collection&gt; 
      &lt;name&gt;<xsl:value-of select="../@name"/>&lt;/name&gt;
       &lt;/collection&gt;
    
  </xsl:when>
 </xsl:choose>
</xsl:for-each>
-->

</xsl:template>
</xsl:stylesheet>