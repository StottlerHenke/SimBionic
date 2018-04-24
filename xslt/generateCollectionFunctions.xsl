<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
	Takes as input the file collections.xml and
	generate the file functions.xsl which has the 
	function isCollection() that will indicate
	that a particular "type in the Taskguide schema"
	define a collection of object 
-->
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
>

<xsl:output method="text" />
<xsl:output method="html" indent="yes" name="html" />

<xsl:template match="/">
<xsl:result-document href="functions.xsl" >&lt;?xml version="1.0" encoding="ISO-8859-1"?&gt;
<!-- write the file header -->


&lt;xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
xmlns:foo="http://www.StottlerHenke.com/datamontage"
&gt;
<!-- end file header -->
<!-- start the function definition -->
&lt;xsl:function name="foo:isCollection"&gt;
 &lt;xsl:param name="modelName"/&gt;
<xsl:if test="count(collections/collection)>0">
   &lt;xsl:choose&gt;

<!-- write of the cases for the switch -->
<xsl:for-each select="collections/collection">
  <xsl:variable name="modelElement" select="name"/>
  <xsl:variable name="model" select="normalize-space($modelElement)"/>
  &lt;xsl:when test="$modelName = '<xsl:value-of select="$model"/>'"&gt;1&lt;/xsl:when&gt;
</xsl:for-each>

<!-- end the function -->
  &lt;xsl:otherwise&gt;0&lt;/xsl:otherwise&gt;
 &lt;/xsl:choose&gt;
</xsl:if>
&lt;/xsl:function&gt;

<!-- start the function definition -->
&lt;xsl:function name="foo:getCollectionTag"&gt;
 &lt;xsl:param name="modelName"/&gt;
<xsl:if test="count(collections/collection)>0"> 
   &lt;xsl:choose&gt;

<!-- write of the cases for the switch -->
<xsl:for-each select="collections/collection/tag">
  <xsl:variable name="modelElement" select="../name"/>
  <xsl:variable name="tagElement" select="."/>
  <xsl:variable name="model" select="normalize-space($modelElement)"/>
  <xsl:variable name="tagName" select="normalize-space($tagElement)"/>
  &lt;xsl:when test="$modelName = '<xsl:value-of select="$model"/>'"&gt;<xsl:value-of select="$tagElement"/>&lt;/xsl:when&gt;
</xsl:for-each>
<!-- end the function -->
  &lt;xsl:otherwise&gt;"error"&lt;/xsl:otherwise&gt;
 &lt;/xsl:choose&gt;
</xsl:if>
&lt;/xsl:function&gt;   
&lt;/xsl:stylesheet&gt;

</xsl:result-document>
</xsl:template>
</xsl:stylesheet>