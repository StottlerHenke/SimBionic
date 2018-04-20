<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
<!--
File: simbionic.xsl
Stottler Henke Associates, Inc.  (c) 2018. All rights reserved.
Jim Ong
Date: 4/14/18

This eXtensible Stylesheet Language (XSL) file specifies transforms 
for generating an HTML report from a SimBionic project file in XML format.

-->

<xsl:template match="/">
  <html>
  <head>
  <title>SimBionic Project Summary</title>
  <style>
  body {font-family: Calibri;}
  h1   {font-family: Calibri;}
  h2   {font-family: Calibri;}
  h3   {font-family: Calibri;}
  p    {font-family: Calibri;}
  img  {border: 1px solid black;}
  td   {vertical-align: top}
  table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
	padding: 4px;
  }
  span.behaviorName {font-family: Calibri; font-size: 14pt; font-weight: bold;}
  </style>
  </head>
  
  <body>
  <h1>SimBionic Project Summary</h1>

  <!-- Display actions not included in any action folder as an HTML table.
       Then, for each action folder, display the folder name,
	   followed by its actions as an HTML table.
   -->
   
  <hr/>
  <h1>Actions</h1>
  
  <table>
    <xsl:apply-templates select="project/actions/action"/>
  </table>
  
  <xsl:for-each select="project/actions/actionFolder">
    <br/>
    <h2><xsl:value-of select="name"/></h2>
	<xsl:if test="count(actionChildren/action) = 0"><i>None</i></xsl:if>
    <table>
	  <xsl:apply-templates select="actionChildren/action"/>
	</table>
  </xsl:for-each>
  
  <!-- Display list of predicates not included in any predicate folder as an HTML table.
       Then, for each predicat folder, display the folder name, 
	   followed by its predicates as an HTML table.
   -->
   
  <hr/>
  <h1>Predicates</h1>

  <table>
    <xsl:apply-templates select="project/predicates/predicate"/>
  </table>
  
  <xsl:for-each select="project/predicates/predicateFolder">
    <br/>
    <h2><xsl:value-of select="name"/></h2>
	<xsl:if test="count(predicateChildren/predicate) = 0"><i>None</i></xsl:if>
    <table>
	  <xsl:apply-templates select="predicateChildren/predicate"/>
	</table>
  </xsl:for-each>


  <!-- Display global variables in an HTML table
   -->
   
  <hr/>
  <h1>Global Variables</h1>  
  <table>
  <xsl:for-each select="project/globals/global">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td><xsl:value-of select="substring-after(type, 'java.lang.')"/></td>
      <td><xsl:value-of select="substring(initial, 1, 40)"/><xsl:if test="string-length(initial) > 40">...</xsl:if></td> 
    </tr>
  </xsl:for-each>
  </table>

  <!-- Display constants in an HTML table
   -->
      
  <hr/>
  <h1>Constants</h1>  
  <table>
  <xsl:for-each select="project/constants/constant">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td><xsl:value-of select="substring-after(type, 'java.lang.')"/></td>
	  <td><xsl:value-of select="substring(value, 1, 40)"/><xsl:if test="string-length(value) > 40">...</xsl:if></td>
    </tr>
  </xsl:for-each>
  </table>
  
   <!-- Display list of behavior names and descriptions.
        First display names of behaviors not included in any behavior folder.
		Then, display names of behaviors, grouped by behavior folder.
        Behavior names are hyperlinked to an anchor that describes the behavior in more detail.
   -->
   
  <hr/>
  <h1>Behavior List</h1>
  
  <table>
    <xsl:apply-templates select="project/behaviors/behavior" mode="short"/>
  </table>
  
  <xsl:for-each select="project/behaviors/behaviorFolder">
    <p/>
    <h2><xsl:value-of select="name"/></h2>
    <table>
    <xsl:apply-templates select="behaviorChildren/behavior" mode="short"/>
	</table>
  </xsl:for-each>
 
  <hr/> 
  
  <!-- Display list of JavaScript files, one filename per line
       DIsplay list of Java classes, one classname per line.
   -->
  
  <h1>JavaScript Files</h1>
  <xsl:if test="count(project/javaScript/jsFiles/jsFile) = 0"><i>None</i></xsl:if>
  <xsl:for-each select="project/javaScript/jsFiles/jsFile">
    <xsl:value-of select="current()"/><br/>
  </xsl:for-each>

  <br/><br/> 
  
  <h1>Java Classes</h1>
  <xsl:if test="count(project/javaScript/importedJavaClasses/importedJavaClass) = 0"><i>None</i></xsl:if>
  <xsl:for-each select="project/javaScript/importedJavaClasses/importedJavaClass">
    <xsl:value-of select="current()"/><br/>
  </xsl:for-each>


  <!-- Display details of each behavior not included in a behavior folder.
       Then, for each behavior folder, display the details of each behavior in the folder.
  -->
  
  <hr/>  
  <h1>Behaviors</h1>
  <xsl:apply-templates select="project/behaviors/behavior"/>
  <xsl:for-each select="project/behaviors/behaviorFolder">
    <hr/>
    <h2>Behavior Folder: <xsl:value-of select="name"/></h2>
    <xsl:apply-templates select="behaviorChildren/behavior"/>
  </xsl:for-each>
 
  </body>
  </html>
</xsl:template>

<!-- Display behavior in short mode as a <tr> element. 
     <td> elements for hyperlinked name, description 
-->
 
<xsl:template match="behavior" mode="short">
  <tr>
    <td>
	  <i>
	    <a>
		  <xsl:attribute name="href">
	        <xsl:value-of select="concat('#btn_', name)"/>
		  </xsl:attribute>
	      <xsl:value-of select="name"/>
		</a>
	  </i>
	</td>
	<td>
	  <xsl:value-of select="description"/>
	</td> 
  </tr>
</xsl:template>	


<!-- Display behavior details: 
       name, parameters, description, and each of its polymorphisms.
 -->
 
<xsl:template match="behavior">
    <hr/>
	<a>
	  <xsl:attribute name="name">
	    <xsl:value-of select="concat('btn_', name)"/>
	  </xsl:attribute>
	</a>
    <span class="behaviorName"><xsl:value-of select="name"/></span>
    (
	  <xsl:for-each select="parameters/param">
	    <xsl:value-of select="name"/>
		<xsl:if test="position() &lt; last()">, </xsl:if>
	  </xsl:for-each>
	)
    <br/><xsl:value-of select="description"/>
	<br/><xsl:apply-templates select="polys/poly"/>
</xsl:template>

<!-- For each of a behavior's polymorphisms, display diagram, local variables.
 -->

<xsl:template match="poly">
  <br/>
  Polymorphism <xsl:value-of select="position()"/>: 
    <b><xsl:for-each select="indices"><xsl:value-of select="index"/></xsl:for-each></b>
  <br/><br/>
  <img>
    <xsl:attribute name="src">
	  <xsl:value-of select="concat('btn_', ../../name, '_', position(), '.png')"/>
	</xsl:attribute>
	<xsl:attribute name="alt">
	  <xsl:value-of select="concat('btn_', ../../name, '_', position(), '.png')"/>
	</xsl:attribute>
  </img>
  <br/><br/>
	  
  <b>Local Variables: </b>
  <xsl:if test="count(locals/local) = 0"><i>None</i></xsl:if>
  <table>
  <xsl:for-each select="locals/local">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td><xsl:value-of select="substring-after(type, 'java.lang.')"/></td>
    </tr>
  </xsl:for-each>
  </table>
</xsl:template>


<!-- For each action, display a <tr> element.
    Display a <td> for the action's name, parameter list, description  
 -->
 
<xsl:template match="action">
      <tr>
        <td><i><xsl:value-of select="name"/></i></td>
		<td>
		  <xsl:for-each select="parameters/param">
		    <xsl:value-of select="name"/><br/>
          </xsl:for-each>
		</td>
        <td><xsl:value-of select="description"/></td> 
      </tr>
</xsl:template>

<!-- For each predicate, display a <tr> element.
    Display a <td> for the predicate's name, return type, parameter list, description  
 -->
 
<xsl:template match="predicate">
      <tr>
        <td><i><xsl:value-of select="name"/></i></td>
		<td><xsl:value-of select="substring-after(returnType, 'java.lang.')"/></td>
		<td>
		  <xsl:for-each select="parameters/param">
		    <xsl:value-of select="name"/><br/>
          </xsl:for-each>
		</td>
        <td><xsl:value-of select="description"/></td> 
      </tr>
</xsl:template>


</xsl:stylesheet> 