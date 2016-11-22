package com.stottlerhenke.simbionic.common.xmlConverters.sax.writers;


import java.awt.Color;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URL;
import java.util.Calendar;


public class Utils {

   static public void writeStartTag(String tag, String attributes, PrintWriter writer, int indent) {
      writeIndent(writer,indent);
      writer.print("<"+tag);
      if (attributes != null) {
         writer.print(" " + attributes);
      }
      writer.println(" >");
   }
   
   static public void writeStartTag(String tag, PrintWriter writer, int indent) {
      writeStartTag(tag,writer,indent,true);
   }
   
   static public void writeStartTag(String tag, PrintWriter writer, int indent,boolean newLine) {
      writeIndent(writer,indent);
      writer.print("<"+tag+">");
      if (newLine) {
         writer.println();
      }
   }
   
   static public void writeEndTag(String tag, PrintWriter writer, int indent) {
      writeEndTag(tag,writer,indent,true);
   }
   
   static public void writeEndTag(String tag, PrintWriter writer, int indent,boolean newLine) {
      writeIndent(writer,indent);
      writer.print("</"+tag+">");
      if (newLine) {
         writer.println();
      }
   }
   
   static public void writeIndent(PrintWriter writer, int indent) {
      if (true)return;
      for (int i=0 ; i < indent ; i++) {
         writer.print("\t");
      }
   }
   
   /**
    * adds a child to the element with the given name and the given value.&nbsp;If
    * value is null, nothing is done
    * @param elem
    * @param tagName
    * @param value -- non-null value
    */
   public static  void writeField(String tagName, BigInteger value, PrintWriter writer, int indent) {
      if (value==null) return;
      writeField(tagName,value.toString(),writer,indent); 
   }
   
   /**
    * adds a child to the element with the given name and the given value.&nbsp;If
    * value is null, nothing is done
    * @param elem
    * @param tagName
    * @param value
    */
   public static  void writeField(String tagName, Boolean value, PrintWriter writer, int indent) {
      if (value==null) return;
      writeField(tagName,value.toString(),writer,indent);
   }
   
   
   /**
    * adds a child to the element with the given name and the given value.&nbsp;If
    * value is null, nothing is done
    * @param elem
    * @param tagName
    * @param value
    */
   public static  void writeField(String tagName, Double value,PrintWriter writer, int indent) {
      if (value == null) return;
      writeField(tagName,value.toString(),writer,indent);
   }
   
   /**
    * adds a child to the element with the given name and the given value.&nbsp;If
    * value is null, nothing is done
    * @param elem
    * @param tagName
    * @param value
    */
   public static  void writeField(String tagName, Integer value,PrintWriter writer, int indent) {
      if (value== null) return;
      writeField(tagName,value.toString(),writer,indent); 
   }
   
   /**
    * adds a child to the element with the given name and the given value.&nbsp;If
    * value is null, nothing is done
    * @param elem
    * @param tagName
    * @param value
    */
   public static  void writeField(String tagName, String value, PrintWriter writer, int indent) {
      if (value==null) return;   
      writeIndent(writer,indent);
      writeStartTag(tagName,writer,indent,false);
      writer.print(normalizeTextToHTML(value));
      writeEndTag(tagName,writer,-1,true);
   }
   
   
   /** replace &, <, > and ", \t, \n for &amp;, &lt; &gt;, &guot; &amp;#52;t, &amp;#52;t**/
   public static String normalizeTextToHTML(String text) {
      if (text == null) {
         return text; 
      }
      else {
         return text.replaceAll("&", "&amp;")
         .replaceAll("<","&lt;")
         .replaceAll(">", "&gt;")
         .replaceAll("\"","&quot;");
         //.replaceAll("\t","&amp;#52;t")
         //.replaceAll("\n","&amp;#52;n");
      }
   }
 
}
