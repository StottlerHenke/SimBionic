<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
<!--
File: simbionic.xsl
Stottler Henke Associates, Inc.  (c) 2018. All rights reserved.
Jim Ong
Date: 4/22/18

This eXtensible Stylesheet Language (XSL) file specifies transforms 
for generating an HTML report from a SimBionic project file in XML format.

-->
<xsl:variable name="apos">'</xsl:variable>
<xsl:variable name="space">&#160;</xsl:variable>


<xsl:template match="/">
  <html>
  <head>
  <title>SimBionic Project Listing</title>
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
	padding: 3px;
  }
  div.btnNodeDetails {
    padding: 5px;
	background-color: #F8F8F8;
    display: none;
    height: 40%;
	width: 40%;
	overflow: scroll;
    border: 3px solid;
    position: fixed;
    bottom: 30%;
    right: 0;}
  span.behaviorName {font-family: Calibri; font-size: 14pt; font-weight: bold;}
  </style>
  
  <script type="text/javascript">
  <xsl:text>
  
  <!-- ID of div element currently displayed
   --> 
  var divIdOfCurrentBtnNode = null;
  
  <!-- if user clicks on a node <area> for which details are already displayed, hide details
       otherwise, hide details of current node and show the details of the new node that the user clicked on
   -->
  function showBtnNodeDetails(divID) {

	if (divIdOfCurrentBtnNode == null) {
      var newDiv = document.getElementById(divID);
      newDiv.style.display = "block";
	  divIdOfCurrentBtnNode = divID;	  
	}
    else if (divID == divIdOfCurrentBtnNode) {
      var div1 = document.getElementById(divID);
      div1.style.display = "none";
	  divIdOfCurrentBtnNode = null;
	}
	else {
      var currentDiv = document.getElementById(divIdOfCurrentBtnNode);
      currentDiv.style.display = "none";
      var newDiv = document.getElementById(divID);
      newDiv.style.display = "block";
	  divIdOfCurrentBtnNode = divID;
	}
  } 
  
  </xsl:text>
  </script>
  
  </head>
  
  <body>
  <h1>SimBionic Project Listing</h1>

  <!-- Display list of behavior names and descriptions.
        Behavior names are hyperlinked to an anchor that describes the behavior in more detail.
   -->
   
  <hr/>
  <h1>Behavior Summary</h1>
  
  <!-- summary of behaviors not in any folder 
   -->
  <table>
    <xsl:apply-templates select="project/behaviors/behavior" mode="summary"/>
  </table>
  
  <!-- summary of behaviors grouped by folder 
   -->
  <xsl:for-each select="project/behaviors/behaviorFolder">
    <p/>
    <h2><xsl:value-of select="name"/></h2>
    <table>
      <xsl:apply-templates select="behaviorChildren/behavior" mode="summary"/>
	</table>
  </xsl:for-each>
 

  <hr/>
  <h1>Actions</h1>
  
  <!-- summary of actions not in any folder 
   -->
  <table>
    <xsl:apply-templates select="project/actions/action"/>
  </table>
  
  <!-- summary of actions grouped by folder 
   -->
  <xsl:for-each select="project/actions/actionFolder">
    <br/>
    <h2><xsl:value-of select="name"/></h2>
	<xsl:if test="count(actionChildren/action) = 0"><i>None</i></xsl:if>
    <table>
	  <xsl:apply-templates select="actionChildren/action"/>
	</table>
  </xsl:for-each>
  
   
  <hr/>
  <h1>Predicates</h1>

  <!-- summary of predicates not in any folder 
   -->
  <table>
    <xsl:apply-templates select="project/predicates/predicate"/>
  </table>
  
  <!-- summary of predicates grouped by folder 
   -->
  <xsl:for-each select="project/predicates/predicateFolder">
    <br/>
    <h2><xsl:value-of select="name"/></h2>
	<xsl:if test="count(predicateChildren/predicate) = 0"><i>None</i></xsl:if>
    <table>
	  <xsl:apply-templates select="predicateChildren/predicate"/>
	</table>
  </xsl:for-each>


  <!-- summary of global variables
   -->
    
  <hr/>
  <h1>Global Variables</h1>  
  <table>
    <xsl:apply-templates select="project/globals/global"/>
  </table>

  <!-- summary of constants
   -->
      
  <hr/>
  <h1>Constants</h1>  
  <table>
    <xsl:apply-templates select="project/constants/constant"/>
  </table>
  
  
  <!-- Display list of JavaScript files, one filename per line
   -->
 
  <hr/> 
  <h1>JavaScript Files</h1>
  <xsl:if test="count(project/javaScript/jsFiles/jsFile) = 0"><i>None</i></xsl:if>
  <xsl:for-each select="project/javaScript/jsFiles/jsFile">
    <xsl:value-of select="current()"/><br/>
  </xsl:for-each>

  <!-- DIsplay list of Java classes, one classname per line.
   -->
   
  <br/><br/> 
  <h1>Java Classes</h1>
  <xsl:if test="count(project/javaScript/importedJavaClasses/importedJavaClass) = 0"><i>None</i></xsl:if>
  <xsl:for-each select="project/javaScript/importedJavaClasses/importedJavaClass">
    <xsl:value-of select="current()"/><br/>
  </xsl:for-each>


  <!-- Display details of each behavior not included in a behavior folder.
   -->
  
  <hr/>  
  <h1>Behaviors</h1>
  <xsl:apply-templates select="project/behaviors/behavior"/>
  
  <!-- for each behavior folder, display the details of each behavior in the folder.
   -->
  <xsl:for-each select="project/behaviors/behaviorFolder">
    <hr/>
    <h2>Behavior Folder: <xsl:value-of select="name"/></h2>
    <xsl:apply-templates select="behaviorChildren/behavior"/>
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
	  <xsl:value-of select="description"/>
	</td> 
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
	<xsl:value-of select="description"/>
	<xsl:apply-templates select="polys/poly"/>
</xsl:template>


<!-- For each behavior polymorphisms, display:
     behavior diagram, hidden div elements containing node details, local variables.
 -->

<xsl:template match="poly">

  <br/><br/><br/>
  Polymorphism <xsl:value-of select="position()"/>: 
  
  <!-- display polymorphism indices, separated by commas
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
  
  <!-- construct unique poly_id from btn name and the 
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
	<xsl:apply-templates select="nodes/actionNodes/actionNode" mode="hotspot"/>
	<xsl:apply-templates select="nodes/compoundActionNode/compoundActionNode" mode="hotspot"/>
	<xsl:apply-templates select="conditions/condition" mode="hotspot"/>
  </map>
  
  <!-- create div element for each behavior node to show details, initially hidden
   -->
  <xsl:apply-templates select="nodes/actionNodes/actionNode" />
  <xsl:apply-templates select="nodes/compoundActionNode/compoundActionNode" />
  <xsl:apply-templates select="conditions/condition" />
  
</xsl:template>

<!-- generates an image map entry for an node in a behavior diagram
 -->
<xsl:template 
  match="actionNode|compoundActionNode/compoundActionNode|condition" 
  mode="hotspot">

  <xsl:variable name="polyPosition" select="count(ancestor::poly/preceding-sibling::poly) + 1" />

  <!-- generate unique ID for node to identify div that displays node details 
   -->	
  <xsl:variable name="nodeDetailsDivId"
	select="concat(local-name(), '_', ancestor::behavior[1]/name, '_', $polyPosition, '_', position())" >
  </xsl:variable>
  
  <area shape='rect'>
    <xsl:attribute name="onclick">
      <xsl:value-of select="concat('showBtnNodeDetails(', $apos, $nodeDetailsDivId, $apos, ')' )"/>
    </xsl:attribute>
	
	<xsl:attribute name="title">
      <xsl:value-of select="$nodeDetailsDivId"/>
    </xsl:attribute>
  
    <xsl:attribute name="coords">
	  <xsl:value-of 
	    select = "concat(
		   cx - 60 div 2, ',', 
           cy - 70 div 2, ',', 
		   cx + 60 div 2, ',',
		   cy + 70 div 2)"
	  />
    </xsl:attribute>
  </area>
  
</xsl:template>

<!-- For each action in a SimBionic project, generate an HTML table row
     that displays: name, parameter list, description
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

<!-- For each predicate in a SimBionic project, generate an HTML table row
     that displays: name, return type, parameter list, description
	 Strip off 'java.lang.' from return types to declutter
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

<!-- For each global variable in a SimBionic project, generate an HTML table row
     that displays: name, type, initial value, description
	 Strip off 'java.lang.' from return types to declutter
	 Truncate initial value to 60 characters. Maybe display ellipses (...).
 -->
 
<xsl:template match="global">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td><xsl:value-of select="substring-after(type, 'java.lang.')"/></td>
      <td><xsl:value-of select="substring(initial, 1, 60)"/>
	      <xsl:if test="string-length(initial) > 60">...</xsl:if>
	  </td> 
	  <td><xsl:value-of select="description"/></td>
    </tr>
</xsl:template>

<!-- For each constant in a SimBionic project, generate an HTML table row
     that displays: name, type, value, description
	 Strip off 'java.lang.' from return types to declutter
	 Truncate initial value to 60 characters. Maybe display ellipses (...).
 -->
 
<xsl:template match="constant">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td><xsl:value-of select="substring-after(type, 'java.lang.')"/></td>
      <td><xsl:value-of select="substring(value, 1, 60)"/>
	      <xsl:if test="string-length(value) > 60">...</xsl:if></td> 
	  <td><xsl:value-of select="description"/></td>
    </tr>
</xsl:template>

<!-- For each local variable in a polymorphism, generate an HTML table row
     that displays: name, type, description
	 Strip off 'java.lang.' from return types to declutter
 -->
 
<xsl:template match="local">
    <tr>
      <td><i><xsl:value-of select="name"/></i></td>
	  <td><xsl:value-of select="substring-after(type, 'java.lang.')"/></td>
	  <td><xsl:value-of select="description"/></td>
    </tr>
</xsl:template>

<!-- “compoundActionNode” seems to be the name of the tag for 1 compound action node 
     as well as the tag name for the set of compound action nodes in a polymorphism.
 -->
 
<!-- For each node, including action nodes (including calls to sub-behaviors), 
     compound action nodes, and condition nodes:
	   generate div element, initially hidden, that displays the node's details
 -->
<xsl:template match="actionNode|condition|compoundActionNode/compoundActionNode">

  <!-- polyPosition1 = position of the poly that contains this node 
   -->
  <xsl:variable 
    name="polyPosition1" 
    select="count(ancestor::poly/preceding-sibling::poly) + 1" />

  <!-- nodeDetailsDivId = unique id of div element, computed from:
       name of element, behavior name, polyPosition1, and position() of the node w/in poly
   -->
  <xsl:variable 
    name="nodeDetailsDivId" 
	select="concat(local-name(), '_', ancestor::behavior[1]/name, '_', $polyPosition1, '_', position())" />
		
  <!-- div element displays node comment, bindings, expression
       Set id attribute of div element to $nodeDetailsDivId
	   Set class attribute of div element to "btnNodeDetails"
   -->
  <div class="btnNodeDetails">
  
    <!-- set HTML anchor and class attributes
	 -->
    <xsl:attribute name="id">
	  <xsl:value-of select="$nodeDetailsDivId" />
	</xsl:attribute>
	
	<xsl:attribute name="class">btnNodeDetails</xsl:attribute>

	<i><xsl:value-of select="comment"/></i>

    <br/><br/>
    <table>
      <xsl:apply-templates select="bindings/binding" />
    </table>
	
    <br/>
    <xsl:value-of select="expr"/>

	<!-- for debugging
	 -->
    <br/><br/>
	<i><xsl:value-of select="$nodeDetailsDivId"/></i>
		
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

<!-- For each (Behavior) param, generate an HTML table row that 
     displays the parameter's name, type, and description
 -->
<xsl:template match="param">
  <tr>
    <td><xsl:value-of select="name" /></td>
    <td><xsl:value-of select="substring-after(type, 'java.lang.')"/></td>
    <td><xsl:value-of select="description" /></td>
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




</xsl:stylesheet> 