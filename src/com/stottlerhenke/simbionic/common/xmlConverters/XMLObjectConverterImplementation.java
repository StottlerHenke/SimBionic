package com.stottlerhenke.simbionic.common.xmlConverters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.StackParser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.readers.SimBionicJavaSAXReader;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.writers.SimBionicJavaSAXWriter;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.writers.Utils;

public class XMLObjectConverterImplementation implements XMLObjectConverterInterface {
   
   private static final String TAG_NAME = "project";

   // Singleton pattern
      
   private XMLObjectConverterImplementation() { 

   }

   
   //singleton
   private static XMLObjectConverterImplementation g_XMLObjectConverter;
   
   public static XMLObjectConverterImplementation getInstance() {
     if (g_XMLObjectConverter == null)
         g_XMLObjectConverter = new XMLObjectConverterImplementation();
     return g_XMLObjectConverter;
   }
   
   
   
   ////////////////////////////////////////////////////////////////////////////
   // Interface
   ////////////////////////////////////////////////////////////////////////////
   /**
    * Deserializes a SimBionicJava object from XML saved in the specified file.
    */
   public SimBionicJava XMLToObject(File source) throws Exception {
      final StackParser stackParser = new StackParser();
      XMLMissionSAXParser parser = new XMLMissionSAXParser(stackParser);
      stackParser.addParser(parser);
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxparser = factory.newSAXParser();
      saxparser.parse(source, getDocHandler(stackParser));
      return parser.getValue();  
   }

   
   /**
    * Deserializes a SimBionicJava object from XML in the give InputStream.
    */
   public SimBionicJava XMLToObject(InputStream source) throws Exception {
      final StackParser stackParser = new StackParser();
      XMLMissionSAXParser parser = new XMLMissionSAXParser(stackParser);
      stackParser.addParser(parser);
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxparser = factory.newSAXParser();
      saxparser.parse(source, getDocHandler(stackParser));
      return parser.getValue();  
   }

   /**
    * Deserializes a ActionsPredicates object from XML located at the specified URL.
    */
   public SimBionicJava XMLToObject(URL source) throws Exception {
      InputStream urlStream = source.openStream();
      SimBionicJava container = XMLToObject(urlStream);
      urlStream.close();
      return container;
   }
   
   DefaultHandler getDocHandler(final StackParser stackParser) {
      return new DefaultHandler () {
         String text = null;
         public void startElement(String uri, String localName, String qName, Attributes att )  {
            try {
               text = null;
               stackParser.startElement(qName, null);
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         
         public void endElement(String uri, String localName, String qName)  {
            try {
               if (text!= null) {
                  stackParser.text(text);
                  text = null;
               }
               stackParser.endElement(qName);
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         
         public void characters (char[] ch, int start, int length) {
            try {
               if (text!= null) {
                  text += new String(ch,start,length); 
               }
               else {
                  text = new String(ch,start,length); 
               }
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
       
      }; 
   }

   /**
    * Serializes the given Procedure object into XML and outputs to the given OutputStream.
    */
   public void ObjectToXML(SimBionicJava missionData, OutputStream output) throws IOException {
      Writer writer = new OutputStreamWriter(output,"UTF-8");
      ObjectToXML(missionData,writer);
      writer.close();
   }
   
   /**
    * Serializes the given Procedure object into XML and writes to the given Writer.
    */
   public void ObjectToXML(SimBionicJava missionData, Writer output) throws IOException{
      PrintWriter printWriter = new PrintWriter(output);
      Utils.writeStartTag(TAG_NAME,  printWriter, 0);
      SimBionicJavaSAXWriter.write(missionData, printWriter,1);
      Utils.writeEndTag(TAG_NAME, printWriter, 0);

   }
   
   public void ObjectToXML(SimBionicJava missionData, String fileName) throws Exception {
      File  file = new File (fileName) ;
      Writer writer = new FileWriter(file);
      ObjectToXML(missionData,writer);
      writer.close();
   }
   
   public SimBionicJava XMLToObject(String fileName) throws Exception {
      File file = new File(fileName);
      SimBionicJava missionData = XMLToObject(file);
      return missionData;
      
   }


   
    class XMLMissionSAXParser extends Parser {
         public XMLMissionSAXParser(StackParser stackParser) {
            super(stackParser,null,0);
         }

         SimBionicJava graphContainerRead;
          /** current helper parser that should process the received tags**/
         private Parser currentReaderHandler = null;
         
         public SimBionicJava getValue () {
            if (graphContainerRead != null) {
               return graphContainerRead;
            }
            else if (currentReaderHandler!= null) {
               return (SimBionicJava)currentReaderHandler.getValue();
            }
            else {
               return null;
            }
         }
         
          /** given a XML element read the DM object associated with the class**/
         public void startElement(String tag, Hashtable tagAttributes) throws Exception {
           // if we are doing some special processing then pass information to the parser 
           if (tag.indexOf(TAG_NAME) >=0) {
             currentReaderHandler = new SimBionicJavaSAXReader(stackParser,tag,tagAttributes,this,0);
              stackParser.addParser(currentReaderHandler);
           }
           else {
              //error?
           }
         }
         
           public void endElement(String tag) throws Exception {
            // if we are doing some special processing then pass information to the parser 
              if (currentReaderHandler != null && !currentReaderHandler.isDone()) {
                 currentReaderHandler.endElement(tag);
                 if (currentReaderHandler.isDone()) {
                    isDone = true;
                    graphContainerRead = (SimBionicJava)currentReaderHandler.getValue();
                    currentReaderHandler = null;
                 }
                 return;
              }         
           }
       }
}
