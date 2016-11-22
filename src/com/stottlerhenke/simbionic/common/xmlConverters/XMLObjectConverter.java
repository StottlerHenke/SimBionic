package com.stottlerhenke.simbionic.common.xmlConverters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;

public class XMLObjectConverter {

      static private XMLObjectConverter instance;
      private XMLObjectConverterInterface implementation;
      
      static public XMLObjectConverter getInstance(){
         if (instance == null)
            instance = new XMLObjectConverter();
         return instance;
      }
      
      private XMLObjectConverter(){
         implementation = XMLObjectConverterImplementation.getInstance();
      }
      
      /**
       * 
       * @param implementation
       */
      public void setImplementation(XMLObjectConverterInterface implementation) {
         this.implementation = implementation;
      }
      /**
       * Loads Procedure from a file.
       */
      public SimBionicJava XMLToObject(File source) throws Exception{
         SimBionicJava graphContainer = implementation.XMLToObject(source);
         System.gc();
         return graphContainer;
      }
      
      public SimBionicJava XMLToObject(String fileName) throws Exception {
         SimBionicJava graphContainer = implementation.XMLToObject(fileName);
         System.gc();
         return graphContainer;  
      }
      
      /**
       * Loads Procedure from an InputStream.
       */
      public SimBionicJava XMLToObject(InputStream source) throws Exception{
         SimBionicJava graphContainer = implementation.XMLToObject(source);
         System.gc();
         return graphContainer;
      }
      
      /**
       * Loads Procedure from a URL.
       */
      public SimBionicJava XMLToObject(URL source) throws Exception{
         SimBionicJava graphContainer = implementation.XMLToObject(source);
         System.gc();
         return graphContainer;
      }
      
      /**
       * Loads Procedure from a zipped xml file. 
       */
      public SimBionicJava zippedXMLToObject(File zippedFile) throws Exception, ZipException, IOException
      {
         FileInputStream in = new FileInputStream(zippedFile);
         SimBionicJava graphContainer = zippedXMLToObject(in);
         in.close();
         return graphContainer;
      }
      
      /**
       * Loads Procedure from an InputStream which reads in zipped XML data.
       */
      public SimBionicJava zippedXMLToObject(InputStream zippedInput) 
         throws Exception, ZipException, IOException
      {
           ZipInputStream in = new ZipInputStream(zippedInput);
           // assume that each zip file has one and only one entry;
           in.getNextEntry();
           return XMLToObject(in);
      }
      
      /**
       * Loads Procedure from the URL that contains the zipped xml file.
       */
      public SimBionicJava zippedXMLToObject(URL zippedFileURL) throws Exception, ZipException, IOException {
         InputStream in = zippedFileURL.openStream();
         SimBionicJava graphContainer = zippedXMLToObject(in);
         in.close();
         return graphContainer;
      }
      
      /**
       * Saves the graph container configurations to the specified output.
       * 
       * @param container
       * @param output
       * @throws Exception
       */
      public void saveXML(SimBionicJava container, OutputStream output) throws Exception{
         implementation.ObjectToXML(container, output);
         System.gc();
      }
      
      /**
       * Saves the graph container configurations to the specified xml file.
       * @param container
       * @param output
       * @throws Exception
       */
      public void saveXML(SimBionicJava container, Writer output) throws Exception{
         implementation.ObjectToXML(container, output);
         System.gc();
      }
      
      /**
       * Saves the graph container configurations to the specified xml file.
       * 
       * @param container
       * @param output
       * @throws Exception
       */
      public void saveXML(SimBionicJava container, File output) throws Exception{
         OutputStream fileOutputStream = new FileOutputStream(output);
         saveXML(container,fileOutputStream);
         fileOutputStream.flush();
         fileOutputStream.close();
      }
      
        /**
         * Saves the graph container configurations to the specified ZIP file.
         * 
         * @param container
         * @param filename name of the ZIP file to save the configuration XML file
         * @throws Exception
         * @throws FileNotFoundException
         * @throws IOException
         * @since 2.1
         */
        public void saveZippedXML(SimBionicJava container, String filename) throws Exception, FileNotFoundException, IOException{
           File file = new File(filename);
           saveZippedXML(container,file);
        }
        
        
        public void saveXML(SimBionicJava container, String filename) throws Exception, FileNotFoundException, IOException{
           implementation.ObjectToXML(container, filename);
        }

       
        /**
         * Saves the graph container configurations to the specified xml file.
         * 
         * @param container
         * @param file
         * @throws Exception
         * @throws FileNotFoundException
         * @throws IOException
         */
        public void saveZippedXML(SimBionicJava container, File file) throws Exception, FileNotFoundException, IOException{
           FileOutputStream xmlOutput = new FileOutputStream(file);
           ZipOutputStream zipXmlOutput = new ZipOutputStream(xmlOutput);
           String entryName = file.getName();
           if (entryName.toLowerCase().endsWith(".zip"))
              entryName = entryName.substring(0, entryName.lastIndexOf('.'))+".xml";
           zipXmlOutput.putNextEntry(new ZipEntry(entryName));
           saveXML(container,zipXmlOutput);
           zipXmlOutput.flush();
           zipXmlOutput.close();
        }
        
        /**
         * write the given given ByteArrayOutputStream into the given xml file
         * 
         * @param containerOut -- result of saving a graph container into a ByteArrayOutputStream
         * @param file
         * @throws Exception
         * @throws FileNotFoundException
         * @throws IOException
         */
        public void saveXML(ByteArrayOutputStream containerOut, File file) throws Exception, FileNotFoundException, IOException{
         FileOutputStream fos = new FileOutputStream(file);
         fos.write(containerOut.toByteArray());
         fos.flush();
         fos.close();
        }

        /**
         * writes the given ByteArrayOutputStream into the given zip file
         * 
         * @param containerOut
         * @param file
         * @throws Exception
         * @throws FileNotFoundException
         * @throws IOException
         */
        public void saveZippedXML(ByteArrayOutputStream containerOut, File file) throws Exception, FileNotFoundException, IOException{
           FileOutputStream xmlOutput = new FileOutputStream(file);
           ZipOutputStream zipXmlOutput = new ZipOutputStream(xmlOutput);
           String entryName = file.getName();
           if (entryName.toLowerCase().endsWith(".zip"))
              entryName = entryName.substring(0, entryName.lastIndexOf('.'))+".xml";
           zipXmlOutput.putNextEntry(new ZipEntry(entryName));
           zipXmlOutput.write(containerOut.toByteArray());
           zipXmlOutput.flush();
           zipXmlOutput.close();
        }
}
