<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
xmlns:DMFn="http://www.StottlerHenke.com/datamontage"
>

<xsl:include href="functions.xsl"/>
<xsl:include href="renames.xsl"/>
<xsl:output method="text" />
<xsl:output method="html" indent="yes" name="html" />

<!-- xslt "variables" are final: once the value is set it cannot be changed -->
<xsl:variable name="package">com.stottlerhenke.simbionic.common.xmlConverters.sax.readers</xsl:variable>



<xsl:template match="/">

<xsl:for-each select="xsd:schema/xsd:complexType">
 <xsl:variable name="filename" select="concat('../src/com/stottlerhenke/simbionic/common/xmlConverters/sax/readers/',@name,'SAXReader','.java')" />
 Creating <xsl:value-of select="$filename"/>
<xsl:result-document href="{$filename}" >
package <xsl:value-of select="$package"/>;
 /*
 * class automatically generated using XSLT translator
 *
 */

import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.StackParser;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.awt.Color;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.basicParsers.*;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.readers.*;
    
<xsl:variable name="className" select="concat(@name,'SAXReader')" />
public class <xsl:value-of select="@name" />SAXReader extends Parser {

<xsl:for-each select="xsd:all/xsd:element">
  /** any order, minOccurs=<xsl:value-of select="@minOccurs" />, type=<xsl:value-of select="@type" /> **/
  public static String <xsl:value-of select="@name" /> = "<xsl:value-of select="@name" />";
  /** id to refer to internally refer to the <xsl:value-of select="@name" /> tag **/
  public static int <xsl:value-of select="@name" />_ID = <xsl:value-of select="position()" />;
</xsl:for-each>
<xsl:variable name="i" select="0"/>
<xsl:for-each select="xsd:sequence/xsd:element">
  /** sequence ,minOccurs=<xsl:value-of select="@minOccurs" />, type=<xsl:value-of select="@type" /> **/
  public static String <xsl:value-of select="@name" /> = "<xsl:value-of select="@name" />";
   /** id to refer to internally refer to the <xsl:value-of select="@name" /> tag **/
  public static int <xsl:value-of select="@name" />_ID = <xsl:value-of select="position()" />;
 </xsl:for-each>

<xsl:for-each select="xsd:choice/xsd:element">
  /** sequence ,minOccurs=<xsl:value-of select="@minOccurs" />, type=<xsl:value-of select="@type" /> **/
  public static String <xsl:value-of select="@name" /> = "<xsl:value-of select="@name" />";
   /** id to refer to internally refer to the <xsl:value-of select="@name" /> tag **/
  public static int <xsl:value-of select="@name" />_ID = <xsl:value-of select="position()" />;
 </xsl:for-each>
<!-- Note: the use of position() is the above code assumes only one of "all,sequence,choice" is the
	 main tag for the class definition -->
<!-- decide whether this class is a "collection" class: a collection class one that stands for
    a set of objects identified by a common tag. For example, GraphModuleXDateTimeModels identifies
    a set of modules by the tag "module".
    For know, this xslt code consideres a class to be a collection if it is a complex type
   having a xsd:sequence with one element that can occur 0 or more times xsd:maxOccurs="unbounded"

   This is an strong assumption that is valid for DM schemas, and help use generate appropriate
   initial code to do the parsing. see getFieldFromCollection template
   Moreover, we correctly assume that in datamontage the use of xsd:sequence is only to mark
   collections, rather than an xml schema where the order of the tags is important

-->
  <xsl:if test="count(xsd:all) > 0">
     <xsl:call-template name="generateNoCollectionClass" >
      <xsl:with-param name="className" select="$className"/>
     </xsl:call-template>
  </xsl:if>

  <xsl:if test="count(xsd:sequence) > 0">
     <xsl:call-template name="generateCollectionClass">
      <xsl:with-param name="className" select="$className"/>
     </xsl:call-template>
  </xsl:if>

  <xsl:if test="count(xsd:choice) > 0">
     <xsl:call-template name="generateChoiceClass">
      <xsl:with-param name="className" select="$className"/>
     </xsl:call-template>
  </xsl:if>
 } <!-- end of class declaration-->
 
</xsl:result-document>
</xsl:for-each> <!-- for each complext type -->
</xsl:template>

<xsl:template name="generateNoCollectionClass">
  <xsl:param name="className"/>
  <xsl:variable name="ObjectClass" select="@name" />
  <xsl:variable name="DMClass" select="DMFn:getDMClass($ObjectClass)"/>
  protected String startTag;
  protected Hashtable startTagAttributes;
  protected <xsl:value-of select="$DMClass" /> readObject;

    
  /** constructor **/
  public <xsl:value-of select="$className"/> (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  <xsl:value-of select="$DMClass" /> ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public <xsl:value-of select="$DMClass" /> getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      <xsl:for-each select="xsd:all/xsd:element">
       <xsl:variable name="fieldName" select="@name" />
       <xsl:variable name="tagFieldName" select="concat($className,'.',@name)" />
       <xsl:variable name="setter" select="concat('set',upper-case(substring($fieldName,1,1)),substring($fieldName,2))"/>
       <xsl:call-template name="requestParsingField">
        <xsl:with-param name="className" select="$className" />
         <xsl:with-param name="setter" select="DMFn:getDMSetterName($DMClass,$setter)" />
        </xsl:call-template>
      </xsl:for-each>
      {
      	//signal error
      }
      
  } //end of startElement method

  public void endElement(String tag) throws Exception {
	  if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error ?
	 }
  }
  
  /**  set the given field property of the object being read **/
  protected  void receiveParsingResult(int property, Object result) {
  	try{
     switch (property) {
      <xsl:for-each select="xsd:all/xsd:element">
       <xsl:variable name="fieldName" select="@name" />
       <xsl:variable name="tagFieldName" select="concat($className,'.',@name)" />
       <xsl:variable name="setter" select="concat('set',upper-case(substring($fieldName,1,1)),substring($fieldName,2))"/>
       <xsl:call-template name="setField">
        <xsl:with-param name="className" select="$className" />
         <xsl:with-param name="setter" select="DMFn:getDMSetterName($DMClass,$setter)" />
         <xsl:with-param name="setterID" select="position()"/>
        </xsl:call-template>
      </xsl:for-each>     
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  
</xsl:template> <!-- end template generateNoCollectionClass -->

<xsl:template name="generateCollectionClass">
  <xsl:param name="className"/>

 <xsl:variable name="typeModel" select="xsd:sequence/xsd:element/@type"/>
 <xsl:variable name="type" select="$typeModel"/>
  <xsl:variable name="DMClass" select="DMFn:getDMClass($type)"/>
 <xsl:variable name="typeReader" select="concat($typeModel,'SAXReader')"/>
  protected String startTag;
  protected Hashtable startTagAttributes;
  List&lt;<xsl:value-of select="$DMClass" />&gt; readObjects;

    
  /** constructor **/
  public <xsl:value-of select="$className"/> (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObjects = new   ArrayList&lt;<xsl:value-of select="$DMClass" />&gt; ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  public List&lt;<xsl:value-of select="$DMClass" />&gt; getValue () {
	  return readObjects;
  }
  
 /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception { 
	  <xsl:choose>
		 <xsl:when test="$type = 'xsd:string'">
		    stackParser.addParser(new StringParser(tag,tagAttributes,this,0)) ;
		 </xsl:when>
		 <xsl:otherwise>
			stackParser.addParser(new <xsl:value-of select="$typeReader"/>(stackParser,tag,tagAttributes,this,0)); 
		 </xsl:otherwise>
	</xsl:choose>
  }
  
  public void endElement(String tag) throws Exception {
	  if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error
	 }
  }
  
  /** collect the result. the property argument is disregarded **/
  protected  void receiveParsingResult(int property, Object result) {
   try{
  	if (result == null) return;
  	readObjects.add((<xsl:value-of select="$DMClass" />)result);
   }
    catch(Exception e){
    	e.printStackTrace();
    }
  }

</xsl:template>  <!-- end template generateCollectionClass -->

<xsl:template name="generateChoiceClass">
  <xsl:param name="className"/>

  <xsl:choose>
  <xsl:when test="xsd:choice[@maxOccurs  = 'unbounded']">
    <xsl:call-template name="generateChoiceClassMultiple">
        <xsl:with-param name="className" select="$className" />
   </xsl:call-template>
  </xsl:when>
  <xsl:when test="xsd:choice[@maxOccurs >= 2]">
    <xsl:call-template name="generateChoiceClassMultiple">
        <xsl:with-param name="className" select="$className" />
   </xsl:call-template>
  </xsl:when>
  <xsl:otherwise>
    <xsl:call-template name="generateChoiceClassSingle">
        <xsl:with-param name="className" select="$className"/>
   </xsl:call-template>
  </xsl:otherwise>
  </xsl:choose>

</xsl:template>  <!-- end template generateChoiceClass -->

<xsl:template name="generateChoiceClassMultiple">
  <xsl:param name="className"/>

 <xsl:variable name="typeModel" select="@name"/>
 <xsl:variable name="type" select="$typeModel"/>
  <xsl:variable name="DMClass" select="DMFn:getDMClass($type)"/>
 <xsl:variable name="typeReader" select="concat($typeModel,'SAXReader')"/>
 
  protected String startTag;
  protected Hashtable startTagAttributes;
  <xsl:choose>
	 <xsl:when test="DMFn:isCompositeCollection($typeModel) = 1"><xsl:value-of select="$DMClass" /> readObjects;</xsl:when>
	 <xsl:otherwise>List&lt;<xsl:value-of select="$DMClass" />&gt; readObjects;</xsl:otherwise>
  </xsl:choose> 
  
  /** constructor **/
  public <xsl:value-of select="$className"/> (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 <xsl:choose>
	 <xsl:when test="DMFn:isCompositeCollection($typeModel) = 1">readObjects = new <xsl:value-of select="$DMClass" />();</xsl:when>
	 <xsl:otherwise>readObjects = new   ArrayList&lt;<xsl:value-of select="$DMClass" />&gt; ();</xsl:otherwise>
	 </xsl:choose>
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  <xsl:choose>
   <xsl:when test="DMFn:isCompositeCollection($typeModel) = 1">public <xsl:value-of select="$DMClass" /> getValue () {
	  return readObjects;
  } </xsl:when>
   <xsl:otherwise>public List&lt;<xsl:value-of select="$DMClass" />&gt; getValue () {
	  return readObjects;
  } </xsl:otherwise>
  </xsl:choose>   
 
 /** given the start of a tag create a parser to transform the content of the tag into a TG object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {
    <xsl:for-each select="xsd:choice/xsd:element">
      <xsl:variable name="typeModel" select="@type"/>
      <xsl:variable name="typeReader" select="concat($typeModel,'SAXReader')"/>
      <xsl:variable name="choiceTag" select="concat($className,'.',@name)"/>
    if (<xsl:value-of select="$choiceTag"/>.equals(tag)) {
      stackParser.addParser(new <xsl:value-of select="$typeReader"/>(stackParser,tag,tagAttributes,this,0));
      return;
    }
       </xsl:for-each>   
  }
  
    public void endElement(String tag) throws Exception {		
	  if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error
	 }
  }
 <xsl:variable name="collectionAccessor" select="DMFn:getCompositeCollectionAccessor($typeModel)"/>
  /** collect the result . The property argument is disregarded **/ 
  protected  void receiveParsingResult(int property, Object result) {
   try{
    if (result == null) return;
	<xsl:choose>
	<xsl:when test="DMFn:isCompositeCollection($typeModel) = 1"> readObjects.<xsl:value-of select="$collectionAccessor"/>.add(result);</xsl:when>
	<xsl:otherwise>readObjects.add((<xsl:value-of select="$DMClass" />)result);</xsl:otherwise>
  	</xsl:choose>
   }
    catch(Exception e){
    	e.printStackTrace();
    }  	
  }
  
</xsl:template>  <!-- end template generateChoiceMultiple -->

<xsl:template name="generateChoiceClassSingle">
  <xsl:param name="className"/>

 <xsl:variable name="typeModel" select="@name"/>
 <xsl:variable name="type" select="$typeModel"/>
  <xsl:variable name="DMClass" select="DMFn:getDMClass($type)"/>
 <xsl:variable name="typeReader" select="concat($typeModel,'SAXReader')"/>
  protected String startTag;
  protected Hashtable startTagAttributes;
  protected <xsl:value-of select="$DMClass"/> readObject;

  /** constructor **/
  public <xsl:value-of select="$className"/> (StackParser stackParserController, String tag, Hashtable tagAttributes,  Parser client, int property) {
     super(stackParserController,client,property);
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns object read by the parser **/
   public <xsl:value-of select="$DMClass"/> getValue () {
	  return readObject;
   }
  
 /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {
    <xsl:for-each select="xsd:choice/xsd:element">
      <xsl:variable name="typeModel" select="@type"/>
      <xsl:variable name="typeReader" select="concat($typeModel,'SAXReader')"/>
      <xsl:variable name="choiceTag" select="concat($className,'.',@name)"/>
    if (<xsl:value-of select="$choiceTag"/>.equals(tag)) {
      stackParser.addParser(new <xsl:value-of select="$typeReader"/>(stackParser,tag,tagAttributes,this,0)) ;
    }
      </xsl:for-each>   
  }
  
  public void endElement(String tag) throws Exception {		
	 if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error
	 }
  }
  
  /** make result be the value returned by this parser. The property argument is disregarded **/
  protected  void receiveParsingResult(int property, Object result) {
   try{
  	readObject = (<xsl:value-of select="$DMClass" />) result;
   }
    catch(Exception e){
    	e.printStackTrace();
    }	
  }
  
</xsl:template>  <!-- end template generateChoiceSingle -->



<!-- 
  template generating code to read an object from a xml tag 
  the written pattern is
     if (field.equals (tag) {
     
     }
     else
-->
<xsl:template name="setField">
     <xsl:param name="className"/>
     <xsl:param name="setter"/>
     <xsl:param name="setterID"/>
     <xsl:variable name="fieldName" select="@name" />
     <xsl:variable name="tagFieldName" select="concat($className,'.',@name)" />
     <xsl:variable name="tagFieldNameID" select="concat($tagFieldName,'_ID')" />	 
     <xsl:choose>
     <xsl:when test="@type = 'xsd:string'">
     	<xsl:choose>
     		<xsl:when test="@maxOccurs = 'unbounded'">
     		case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((List)result);
			break;     		
     		</xsl:when>
     		<xsl:otherwise>
     		case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((String)result);
			break;
     		</xsl:otherwise>
     	</xsl:choose>

     </xsl:when>
     <xsl:when test="@type = 'xsd:positiveInteger'">
	case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((Integer)result);
			break;    	
     </xsl:when>
     <xsl:when test="@type = 'xsd:nonNegativeInteger'">
	case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((Integer)result);
    	    break;
     </xsl:when>
     <xsl:when test="@type = 'xsd:integer'">
	case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((Integer)result);
			break;    
     </xsl:when>
     <xsl:when test="@type = 'xsd:double'">
	case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((Double)result);
			break;    
     </xsl:when>
     <xsl:when test="@type = 'xsd:float'">
	case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((Float)result);
			break;    
     </xsl:when>     
     <xsl:when test="@type = 'xsd:boolean'">
	case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((Boolean)result);
  			break;    
     </xsl:when>
    <xsl:when test="@type = 'dd:colorString'">
 	case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((Color)result);
 			break;   
     </xsl:when>
     <xsl:when test="@type = 'xsd:dateTime'">
 	case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((Date)result);
 			break;   
     </xsl:when>
     <xsl:when test="@type = 'ExitType'">
 	  case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />(ExitType.valueOf((String)result));
 			break;   
     </xsl:when>
     <xsl:when test="@type = 'NullType'">
 	  case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    //ignore the null value ??
 			break;   
     </xsl:when>
     <xsl:when test="@type = 'NoteTypeString'">
 	  case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((String)result);
 			break;   
     </xsl:when>
    <xsl:when test="@name = 'defaultBaseURL'"> <!-- hack very special case -->
 	  case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>:
       	    readObject.<xsl:value-of select="$setter" />((java.net.URL)result);
 			break;   
     </xsl:when>
    <xsl:otherwise>
        <xsl:call-template name="setFieldObject">
         <xsl:with-param name="className" select="$className"/>
         <xsl:with-param name="fieldName" select="$fieldName"/>
         <xsl:with-param name="setter" select="$setter"/>
         <xsl:with-param name="setterID" select="$setterID"/>
        </xsl:call-template>
     </xsl:otherwise>
     </xsl:choose>

</xsl:template>


<xsl:template name="setFieldObject">
  <xsl:param name="className"/>
  <xsl:param name="fieldName"/>
  <xsl:param name="setter"/>
  <xsl:param name="setterID"/>
     <xsl:variable name="typeModel" select="@type"/>
     <xsl:variable name="type" select="$typeModel"/>
     <xsl:variable name="DMClass" select="DMFn:getDMClass($type)"/>
     <xsl:variable name="tagFieldName" select="concat($className,'.',@name)" />
     <xsl:variable name="tagFieldNameID" select="concat($tagFieldName,'_ID')" />
       case <xsl:value-of select="$setterID"/>: //case <xsl:value-of select="$tagFieldNameID"/>
           <xsl:choose>
           <xsl:when test="DMFn:isCollection($typeModel)=1">
            if (result !=null) {
              readObject.<xsl:value-of select="$setter" />((List)result);
            }
           </xsl:when>
          <xsl:otherwise>
           if (result !=null) {
            readObject.<xsl:value-of select="$setter" />((<xsl:value-of select="$DMClass"/>)result);
           }
          </xsl:otherwise>
         </xsl:choose>
          break;    
</xsl:template>

<!-- 
  template generating code to read an object from a xml tag 
  the written pattern is
     if (field.equals (tag) {
     
     }
     else
-->
<xsl:template name="requestParsingField">
     <xsl:param name="className"/>
     <xsl:param name="setter"/>
     <xsl:variable name="fieldName" select="@name" />
     <xsl:variable name="tagFieldName" select="concat($className,'.',@name)" />
     <xsl:variable name="tagFieldNameID" select="concat($tagFieldName,'_ID')" />
	 
     <xsl:choose>
     <xsl:when test="@type = 'xsd:string'">
       	<xsl:choose>
	  		<xsl:when test="@maxOccurs = 'unbounded'">
     			if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
			       stackParser.addParser(new PrimitiveListParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     			}
			     else    		
     		</xsl:when>
     		<xsl:otherwise>
			     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
			     }
			     else 
     		</xsl:otherwise>
     	</xsl:choose>
     </xsl:when>
     <xsl:when test="@type = 'xsd:positiveInteger'">
	 if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else      	
     </xsl:when>
     <xsl:when test="@type = 'xsd:nonNegativeInteger'">
	 if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
     <xsl:when test="@type = 'xsd:integer'">
	 if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
     <xsl:when test="@type = 'xsd:double'">
     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new DoubleParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
     <xsl:when test="@type = 'xsd:float'">
     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new FloatParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>     
     <xsl:when test="@type = 'xsd:boolean'">
	 if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     
     else    
     </xsl:when>
     <xsl:when test="@type = 'dd:colorString'">
	 if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new ColorParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
     <xsl:when test="@type = 'xsd:dateTime'">
     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new DateTimeStringParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
     <xsl:when test="@type = 'ExitType'">
     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new StringParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
     <xsl:when test="@type = 'NullType'"> <!-- read the string value "null" and return the null object -->
     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new NullTypeParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
   <xsl:when test="@type = 'NoteTypeString'">
     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new StringParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
   <xsl:when test="@name = 'defaultBaseURL'"> <!-- hack very special case -->
     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new URLStringParser(tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>)) ;
     }
     else    
     </xsl:when>
     <xsl:otherwise>
        <xsl:call-template name="requestParsingFieldObject">
         <xsl:with-param name="className" select="$className"/>
         <xsl:with-param name="fieldName" select="$fieldName"/>
         <xsl:with-param name="setter" select="$setter"/>
        </xsl:call-template>
     </xsl:otherwise>
     </xsl:choose>

</xsl:template>


<xsl:template name="requestParsingFieldObject">
  <xsl:param name="className"/>
  <xsl:param name="fieldName"/>
  <xsl:param name="setter"/>
     <xsl:variable name="typeModel" select="@type"/>
     <xsl:variable name="type" select="$typeModel"/>
     <xsl:variable name="DMClass" select="DMFn:getDMClass($type)"/>
     <xsl:variable name="typeReader" select="concat($typeModel,'SAXReader')"/>
     <xsl:variable name="xmlElement" select="concat('xml',$fieldName)" />
     <xsl:variable name="space" select="concat(' ','')" />
     <xsl:variable name="tagFieldName" select="concat($className,'.',@name)" />
     <xsl:variable name="tagFieldNameID" select="concat($tagFieldName,'_ID')" />
     if (<xsl:value-of select="$tagFieldName" />.equals(tag)) {
       stackParser.addParser(new <xsl:value-of select="$typeReader"/> (stackParser,tag,tagAttributes,this,<xsl:value-of select="$tagFieldNameID"/>));
     }
    else
    
</xsl:template>

</xsl:stylesheet>
