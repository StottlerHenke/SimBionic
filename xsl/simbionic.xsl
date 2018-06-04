<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!--
File: simbionic.xsl
Copyright (c) 2018 Stottler Henke Associates, Inc. All rights reserved.
Jim Ong
Date: 6/4/18

This eXtensible Stylesheet Language (XSL) file specifies transforms 
for generating an HTML listing from a SimBionic project file in XML format.

-->
<xsl:variable name="apos">'</xsl:variable>
<xsl:variable name="space">&#160;</xsl:variable>
<xsl:variable name="linefeed1">&#10;</xsl:variable>
<xsl:variable name="linefeed">\n</xsl:variable>
<xsl:variable name="period">.</xsl:variable>

<xsl:template match="/">
  <html>
  <head>
  <title>SimBionic Project - <xsl:value-of select="project/projectProperties/projectName"/></title>
  <style>
  body {font-family: Calibri;}
  h1   {font-family: Calibri; font-size: 20pt;}
  h2   {font-family: Calibri; font-size: 16pt;}
  p    {font-family: Calibri;}
  img  {border: 1px solid black;}
  td   {vertical-align: top}
  table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
	padding: 3px;
  }
  tr   {valign: top;}
  div.btnNodeLinkDetails {
    padding: 5px;
	background-color: #F8F8F8;
    display: none;
    height: 40%;
	width: 50%;
	overflow: scroll;
    border: 3px solid;
    position: fixed;
    bottom: 30%;
    right: 0;
  }
  span.projectName  {font-family: Calibri; font-size: 20pt; font-weight: bold;}
  span.behaviorName {font-family: Calibri; font-size: 16pt; font-weight: bold;}
  </style>
  
  <script type="text/javascript"> 
  <!-- ID of div element currently displayed
   --> 
  var divIdOfCurrentBtnNodeOrLink = null;
  
  <!-- if user clicks on a node <area> or link <poly> for which details are already displayed, hide details
       otherwise, hide details of current node or link 
	   and show the details of the new node or link that the user clicked on
   -->
  function showbtnNodeLinkDetails(divID) {

	if (divIdOfCurrentBtnNodeOrLink == null) {
      var newDiv = document.getElementById(divID);
      newDiv.style.display = "block";
	  divIdOfCurrentBtnNodeOrLink = divID;	  
	}
    else if (divID == divIdOfCurrentBtnNodeOrLink) {
      var div1 = document.getElementById(divID);
      div1.style.display = "none";
	  divIdOfCurrentBtnNodeOrLink = null;
	}
	else {
      var currentDiv = document.getElementById(divIdOfCurrentBtnNodeOrLink);
      currentDiv.style.display = "none";
      var newDiv = document.getElementById(divID);
      newDiv.style.display = "block";
	  divIdOfCurrentBtnNodeOrLink = divID;
	}
  } 
  </script>
  
  </head>
  
  <body>
  
<!-- Display Project Property name/value pairs in a table. 
 -->

SimBionic Project
<br/>
<span class="projectName"><xsl:value-of select="project/projectProperties/projectName"/></span>
<hr/>
<p/>
<p/>
  <table>
    <tr><td>Author</td><td><xsl:value-of select="project/projectProperties/author" /></td></tr>
    <tr><td>Description</td><td><xsl:apply-templates select="project/projectProperties/description" /></td></tr>
    <tr><td>Date Last Updated</td><td><xsl:value-of select="project/projectProperties/dateLastUpdate" /></td></tr>
    <tr><td>SimBionic Version</td><td><xsl:value-of select="project/projectProperties/simbionicVersion" /></td></tr>
  </table>

  <h1>Index</h1>
  <p/>
  <ul>
    <li><a href="#behavior_summary">Behavior Summary</a></li>
    <li><a href="#actions">Actions</a></li>
    <li><a href="#predicates">Predicates</a></li>
    <li><a href="#globals">Global Variables</a></li>
    <li><a href="#constants">Constants</a></li>
    <li><a href="#javascript">Javascript Files</a></li>
    <li><a href="#java">Java Classes</a></li>
    <li><a href="#behaviors">Behaviors</a></li>
  </ul>


  <!-- Display list of behavior names and descriptions.
        Behavior names are hyperlinked to an anchor that describes the behavior in more detail.
   -->

  <hr/>
  <a name="behavior_summary"/>
  <h1>Behavior Summary</h1>
  
  <!-- summary of behaviors not in any folder 
   -->
  <table>
    <xsl:apply-templates select="project/behaviors/behavior" mode="summary">
	  <xsl:sort select="name"/>
	</xsl:apply-templates>
  </table>
  
  <!-- summary of behaviors grouped by folder 
   -->
  <xsl:for-each select="project/behaviors/behaviorFolder">
    <xsl:sort select="name"/>
    <p/>
    <h2><xsl:value-of select="name"/></h2>
    <table>
      <xsl:apply-templates select="behaviorChildren/behavior" mode="summary">
	    <xsl:sort select="name"/>
	  </xsl:apply-templates>			 
	</table>
  </xsl:for-each>
 

  <hr/>
  <a name="actions"/>
  <h1>Actions</h1>
  
  <!-- summary of actions not in any folder 
   -->
  <table>
    <xsl:apply-templates select="project/actions/action">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
  </table>
  
  <!-- summary of actions grouped by folder 
   -->
  <xsl:for-each select="project/actions/actionFolder">
    <xsl:sort select="name"/>
    <br/>
    <h2><xsl:value-of select="name"/></h2>
	<xsl:if test="count(actionChildren/action) = 0"><i>None</i></xsl:if>
    <table>
	  <xsl:apply-templates select="actionChildren/action">
	    <xsl:sort select="name"/>
      </xsl:apply-templates>
	</table>
  </xsl:for-each>
  
   
  <hr/>
  <a name="predicates"/>
  <h1>Predicates</h1>

  <!-- summary of predicates not in any folder 
   -->
  <table>
    <xsl:apply-templates select="project/predicates/predicate">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
  </table>
  
  <!-- summary of predicates grouped by folder 
   -->
  <xsl:for-each select="project/predicates/predicateFolder">
    <xsl:sort select="name"/>
    <br/>
    <h2><xsl:value-of select="name"/></h2>
	<xsl:if test="count(predicateChildren/predicate) = 0"><i>None</i></xsl:if>
    <table>
	  <xsl:apply-templates select="predicateChildren/predicate">
        <xsl:sort select="name"/>
      </xsl:apply-templates>
	</table>
  </xsl:for-each>


  <!-- summary of global variables not in any folder
   -->
    
  <hr/>
  <a name="globals"/>
  <h1>Global Variables</h1>  
  <table>
    <xsl:apply-templates select="project/globals/global">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
  </table>
  
  <!-- summary of global variables grouped by folder 
   -->
   
  <xsl:for-each select="project/globals/globalFolder">
    <xsl:sort select="name"/>
    <p/>
    <h2><xsl:value-of select="name"/></h2>
    <table>
      <xsl:apply-templates select="globalChildren/global">
	    <xsl:sort select="name"/>
	  </xsl:apply-templates>			 
	</table>
  </xsl:for-each>
  
  <!-- summary of constants
   -->
      
  <hr/>
  <a name="constants"/>
  <h1>Constants</h1>  
  <table>
    <xsl:apply-templates select="project/constants/constant">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
  </table>
  
  <!-- summary of constants grouped by folder 
   -->
   
  <xsl:for-each select="project/constants/constantFolder">
    <xsl:sort select="name"/>
    <p/>
    <h2><xsl:value-of select="name"/></h2>
    <table>
      <xsl:apply-templates select="constantChildren/constant">
	    <xsl:sort select="name"/>
	  </xsl:apply-templates>			 
	</table>
  </xsl:for-each>
  
  <!-- Display list of JavaScript files, one filename per line
   -->
 
  <hr/> 
  <a name="javascript"/>
  <h1>JavaScript Files</h1>
  <xsl:if test="count(project/javaScript/jsFiles/jsFile) = 0"><i>None</i></xsl:if>
  <xsl:for-each select="project/javaScript/jsFiles/jsFile">
    <xsl:sort select="current()"/>
    <xsl:value-of select="current()"/><br/>
  </xsl:for-each>

  <!-- DIsplay list of Java classes, one classname per line.
   -->
   
  <br/><br/> 
  <a name="java"/>
  <h1>Java Classes</h1>
  <xsl:if test="count(project/javaScript/importedJavaClasses/importedJavaClass) = 0"><i>None</i></xsl:if>
  <xsl:for-each select="project/javaScript/importedJavaClasses/importedJavaClass">
    <xsl:sort select="current()"/>
    <xsl:value-of select="current()"/><br/>
  </xsl:for-each>


  <!-- Display details of each behavior not included in a behavior folder.
   -->
  
  <hr/>  
  <a name="behaviors"/>
  <h1>Behaviors</h1>
  <xsl:apply-templates select="project/behaviors/behavior">
    <xsl:sort select="name"/>
  </xsl:apply-templates>
  
  <!-- for each behavior folder, display the details of each behavior in the folder.
   -->
  <xsl:for-each select="project/behaviors/behaviorFolder">
    <xsl:sort select="name"/>
    <hr/>
    <h2>Behavior Folder: <xsl:value-of select="name"/></h2>
    <xsl:apply-templates select="behaviorChildren/behavior">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
  </xsl:for-each>
 
  </body>
  </html>
</xsl:template>

  
<!-- Display summary of each behavior as a row in a table
 -->
<xsl:template match="behavior" mode="summary">

  <!-- variable btn_ancher = HTML anchor for the details of each behavior
       = return value of named template "btn_id"
   -->
  <xsl:variable name="btn_anchor">
	<xsl:call-template name="btn_id">
      <xsl:with-param name="behavior_name" select="name" />
    </xsl:call-template>	  
  </xsl:variable>


  <tr>
   <!-- col 1 = name of behavior, hyperlinked to the details of the behavior 
	 -->
    <td>
	  <i>
	    <a>
	      <xsl:attribute name="href">
		    <xsl:value-of select="concat('#', $btn_anchor)"/>
	      </xsl:attribute>
	      <xsl:value-of select="name"/>
	    </a>
	  </i>
	</td>
	
	<!-- col 2 = description of the behavior
	 -->
	<td>
	  <xsl:apply-templates select="description"/>
	</td> 
  </tr>
</xsl:template>	


<!-- For each action in a SimBionic project, generate an HTML table row
     that displays: name, parameter list, description
 -->
 
<xsl:template match="action">
  <tr>
    <td><i><xsl:value-of select="name"/></i></td>
    <td>
      <xsl:for-each select="parameters/param">
        <xsl:apply-templates select="."/>
		<br/>
      </xsl:for-each>
    </td>
    <td><xsl:apply-templates select="description"/></td>
  </tr>
</xsl:template>

<!-- For each predicate in a SimBionic project, generate an HTML table row
     that displays: name, return type, parameter list, description
 -->
 
<xsl:template match="predicate">

  <tr>
    <td><i><xsl:value-of select="name"/></i></td>
    <td>
      <xsl:call-template name="substring-after-last">
        <xsl:with-param name="string" select="returnType"/>
		<xsl:with-param name="delimiter" select="$period"/>
      </xsl:call-template>
	</td>
    <td>
      <xsl:for-each select="parameters/param">
        <xsl:apply-templates select="."/><br/>
      </xsl:for-each>
    </td>
    <td><xsl:apply-templates select="description"/></td> 
  </tr>
</xsl:template>

<!-- Display each action or predicate parameter as a span element 
     with a tooltip set to the parameter's type and description.
  -->
<xsl:template match="action/parameters/param|predicate/parameters/param">
	  <xsl:variable name="dataType">
          <xsl:call-template name="substring-after-last">
            <xsl:with-param name="string" select="type"/>
		    <xsl:with-param name="delimiter" select="$period"/>
          </xsl:call-template>	  
	  </xsl:variable>

	  <span>
	  	<xsl:attribute name="title">
          <xsl:value-of select="concat($dataType, ' - ', description)"/>
	    </xsl:attribute>
	  	<xsl:value-of select="name" />
	  </span>
</xsl:template>



<!-- For each global variable in a SimBionic project, generate an HTML table row
     that displays: name, type, initial value, description
	 Truncate initial value to 60 characters. Maybe display ellipses (...).
 -->
 
<xsl:template match="global">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="string" select="type"/>
		  <xsl:with-param name="delimiter" select="$period"/>
        </xsl:call-template>
	  </td>
      <td><xsl:value-of select="substring(initial, 1, 60)"/>
        <xsl:if test="string-length(initial) > 60">...</xsl:if>
      </td> 
      <td><xsl:apply-templates select="description"/></td>
    </tr>
</xsl:template>

<!-- For each constant in a SimBionic project, generate an HTML table row
     that displays: name, type, value, description
	 Truncate initial value to 60 characters. Maybe display ellipses (...).
 -->
 
<xsl:template match="constant">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="string" select="type"/>
		  <xsl:with-param name="delimiter" select="$period"/>
        </xsl:call-template>
	  </td>
      <td><xsl:value-of select="substring(value, 1, 60)"/>
          <xsl:if test="string-length(value) > 60">...</xsl:if>
      </td> 
      <td><xsl:apply-templates select="description"/></td>
    </tr>
</xsl:template>

<!-- For each local variable in a polymorphism, generate an HTML table row
     that displays: name, type, description
 -->
 
<xsl:template match="local">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="string" select="type"/>
		  <xsl:with-param name="delimiter" select="$period"/>
        </xsl:call-template>
	  </td>
      <td><xsl:apply-templates select="description"/></td>
    </tr>
</xsl:template>

<!-- Display behavior details: 
      behavior name, parameters, description, and each of its polymorphisms.
 -->
 
<xsl:template match="behavior">
    <hr/>
	
	<!-- HTML anchor for behavior details = return value of named template "btn_id"
	 -->
	<a>
	  <xsl:attribute name="name">
	  	<xsl:call-template name="btn_id">
          <xsl:with-param name="behavior_name" select="name" />
        </xsl:call-template>
	  </xsl:attribute>
	</a>
	
	<!-- Behavior name
	 -->
	BTN <xsl:value-of select="concat(position(), ':', $space)"/>
    <span class="behaviorName"><xsl:value-of select="name"/></span>

    <!-- Table of behavior parameters
	  -->
	<br/><br/>
	<table>
	  <xsl:apply-templates select="parameters/param"/>
	</table>
	
    <br/>
	<xsl:apply-templates select="description"/>
	<xsl:apply-templates select="polys/poly"/>
</xsl:template>


<!-- For each behavior polymorphisms, display:
     behavior diagram, hidden div elements containing node details, local variables.
 -->

<xsl:template match="poly">

  <br/><br/><br/>
  Polymorphism <xsl:value-of select="position()"/>: 
  
  <!-- Display polymorphism indices, separated by commas
   -->
  <b>
	<xsl:for-each select="indices">
	  <xsl:value-of select="index"/>
	  <xsl:if test="position() &lt; last()">,</xsl:if>
	</xsl:for-each>
  </b>
	
  <!-- Display local variables in an HTML table, one variable per row
   -->
  <br/><br/>
  <b>Local Variables: </b>
  <xsl:if test="count(locals/local) = 0"><i>None</i></xsl:if>
  <table>
    <xsl:apply-templates select="locals/local"/>
  </table>	
  
  <!-- Construct unique poly_id from btn name and the 
       position() of the poly within the btn's list of polys
   -->
  <xsl:variable name="poly_id" 
    select="concat('btn_', ../../name, '_', position())" />
		  
  <!-- img element displays screen capture of the behavior diagram
       Name of image file = poly_id + .png.
	   Name of image map = poly_id + .map.
   -->
  <br/><br/>
  <img>
    <xsl:attribute name="src">
	  <xsl:value-of select="concat($poly_id, '.png')"/>
	</xsl:attribute>
	<xsl:attribute name="alt">
	  <xsl:value-of select="concat($poly_id, '.png')"/>
	</xsl:attribute>
	<xsl:attribute name="usemap">
	  <xsl:value-of select="concat('#', $poly_id, '.map')"/>
	</xsl:attribute>
  </img>
  
  <!-- Generate an entry in the image map for each 
         action node (including sub-behavior invocations), 
		 compound action, and condition.
  	   An image map specifies a hot spot for each node in the diagram.
   -->
  <map>
    <xsl:attribute name="name">
	  <xsl:value-of select="concat($poly_id, '.map')"/>
    </xsl:attribute>
	<xsl:apply-templates select="connectors/start/connectors/connector" mode="hotspot"/>
	<xsl:apply-templates select="nodes/actionNodes/actionNode" mode="hotspot"/>
	<xsl:apply-templates select="nodes/compoundActionNode/compoundActionNode" mode="hotspot"/>
	<xsl:apply-templates select="conditions/condition" mode="hotspot"/>
  </map>
  
  <!-- Generate div element for each behavior node to show details, initially hidden
   -->
  <xsl:apply-templates select="connectors/start/connectors/connector" />
  <xsl:apply-templates select="nodes/actionNodes/actionNode" />
  <xsl:apply-templates select="nodes/compoundActionNode/compoundActionNode" />
  <xsl:apply-templates select="conditions/condition" />
  
</xsl:template>


<!-- For each (Behavior) param, generate an HTML table row that 
     displays the parameter's name, type, and description
 -->
<xsl:template match="behavior/parameters/param">
  <tr>
    <td><xsl:value-of select="name" /></td>
	<td>
      <xsl:call-template name="substring-after-last">
        <xsl:with-param name="string" select="type"/>
		<xsl:with-param name="delimiter" select="$period"/>
      </xsl:call-template>
	</td>
	<td><xsl:apply-templates select="description" /></td>
  </tr>
</xsl:template>

<!-- generates an image map entry for a link in a behavior diagram
 -->
<xsl:template match="connector" mode="hotspot">
  <xsl:variable name="polyPosition" select="count(ancestor::poly/preceding-sibling::poly) + 1" />

  <!-- generate unique ID for node to identify div that displays node details 
   -->	
  <xsl:variable name="nodeLinkDetailsDivId"
	select="concat(local-name(), '_', ancestor::behavior[1]/name, '_', $polyPosition, '_', position())" >
  </xsl:variable>
  
  <area shape='poly'>
    <xsl:attribute name="onclick">
      <xsl:value-of select="concat('showbtnNodeLinkDetails(', $apos, $nodeLinkDetailsDivId, $apos, ')' )"/>
    </xsl:attribute>
	
	<xsl:attribute name="title">
      <xsl:value-of select="$nodeLinkDetailsDivId"/>
    </xsl:attribute>
  
    <xsl:attribute name="coords">
      <xsl:value-of
        select = "concat(
          (startX)-4, ',',
          (startY)-4, ',',
          startX+4, ',',
          (startY)-4, ',',
          endX+4, ',',
          endY+4, ',',
          (endX)-4,   ',',
          endY+4)"
      />
    </xsl:attribute>
  </area>
</xsl:template>

  
  
<!-- generates an image map entry for a node in a behavior diagram
 -->
<xsl:template 
  match="actionNode|compoundActionNode/compoundActionNode|condition" 
  mode="hotspot">

  <xsl:variable name="polyPosition" select="count(ancestor::poly/preceding-sibling::poly) + 1" />

  <!-- generate unique ID for node to identify div that displays node details 
   -->	
  <xsl:variable name="nodeLinkDetailsDivId"
	select="concat(local-name(), '_', ancestor::behavior[1]/name, '_', $polyPosition, '_', position())" >
  </xsl:variable>
	
  <area shape='rect'>
    <xsl:attribute name="onclick">
      <xsl:value-of select="concat('showbtnNodeLinkDetails(', $apos, $nodeLinkDetailsDivId, $apos, ')' )"/>
    </xsl:attribute>
	
	<xsl:attribute name="title">
      <xsl:value-of select="$nodeLinkDetailsDivId"/>
    </xsl:attribute>
  
    <xsl:attribute name="coords">
      <xsl:value-of
        select = "concat(
          cx - width div 2, ',',
          cy - height div 2, ',',
          cx + width div 2, ',',
          cy + height div 2)"
      />
    </xsl:attribute>
  </area>
  
</xsl:template>

<!-- “compoundActionNode” seems to be the name of the tag for 1 compound action node 
     as well as the tag name for the set of compound action nodes in a polymorphism.
 -->
 
<!-- For each node, including action nodes (including calls to sub-behaviors), 
     compound action nodes, and condition nodes:
	   generate div element, initially hidden, that displays the node's details
 -->
<xsl:template match="actionNode|compoundActionNode/compoundActionNode|condition|connector">

  <!-- polyPosition1 = position of the poly that contains this node 
   -->
  <xsl:variable 
    name="polyPosition1" 
    select="count(ancestor::poly/preceding-sibling::poly) + 1" />

  <!-- nodeLinkDetailsDivId = unique id of div element, computed from:
       name of element, behavior name, polyPosition1, and position() of the node w/in poly
   -->
  <xsl:variable name="nodeLinkDetailsDivId" 
	select="concat(local-name(), '_', ancestor::behavior[1]/name, '_', $polyPosition1, '_', position())" />
		
  <!-- div element displays node or link comment, bindings, and expression (for nodes)
       Set id attribute of div element to $nodeLinkDetailsDivId
	   Set class attribute of div element to "btnNodeLinkDetails"
   -->
  <div class="btnNodeLinkDetails">
  
    <!-- set HTML anchor and class attributes
	 -->
    <xsl:attribute name="id">
      <xsl:value-of select="$nodeLinkDetailsDivId" />
    </xsl:attribute>
	
    <xsl:attribute name="class">btnNodeLinkDetails</xsl:attribute>

    <i><xsl:apply-templates select="comment" /></i>
    <p/>
	
    <table>
      <xsl:apply-templates select="bindings/binding" />
    </table>
    <p/>
	
	<!-- connectors (links) do not have an expr tag
	 -->
    <xsl:value-of select="expr"/>
    <p/>

    <!-- for debugging
      -->
    <i><xsl:value-of select="$nodeLinkDetailsDivId"/></i>
		
  </div>
</xsl:template>

<!-- For each binding, generate an HTML table row that 
     displays the name of the variable and the expression 
 -->
<xsl:template match="binding">
  <tr>
    <td><xsl:value-of select="var" /></td>
    <td><xsl:value-of select="expr"/></td>  
  </tr>
</xsl:template>


<!-- generate a unique ID for the behavior from the behavior name.
     'btn_' prefix indicates that the ID identifies a BTN.
	 This prefix is intended to avoid (reduce?) name collisions.
 -->
<xsl:template name="btn_id">
  <xsl:param name="behavior_name" />
  <xsl:value-of select="concat('btn_', $behavior_name)" />
</xsl:template>



<!-- call named template insertBreaks for each element containing
     long text that might contain newline characters.
 -->
 
<xsl:template match="comment|description">
  <xsl:call-template name="insertBreaks">
    <xsl:with-param name="pText" select="."/>
  </xsl:call-template>
</xsl:template>

<!-- replace newlines with <br />
 -->

<xsl:template match="text()" name="insertBreaks">
  <xsl:param name="pText" select="."/>

  <xsl:choose>
    <xsl:when test="not(contains($pText, '&#xA;'))">
      <xsl:copy-of select="$pText"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="substring-before($pText, '&#xA;')"/>
      <br />
      <xsl:call-template name="insertBreaks">
        <xsl:with-param name="pText" 
		    select="substring-after($pText, '&#xA;')"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="substring-after-last">
  <xsl:param name="string" />
  <xsl:param name="delimiter" />
  <xsl:choose>
    <xsl:when test="contains($string, $delimiter)">
      <xsl:call-template name="substring-after-last">
        <xsl:with-param name="string"
          select="substring-after($string, $delimiter)" />
        <xsl:with-param name="delimiter" select="$delimiter" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="$string" /></xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet> 