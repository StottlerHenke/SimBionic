package com.stottlerhenke.simbionic.common.xmlConverters;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;

import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;


public interface XMLObjectConverterInterface {
   /**
    * Deserializes a DM Graph container object from XML saved in the specified file.
    */
   public SimBionicJava XMLToObject(File source) throws Exception ;
   
   public SimBionicJava XMLToObject(String filename) throws Exception ;
   
   /**
    * Deserializes a DM Graph container object from XML in the give InputStream.
    */
   public SimBionicJava XMLToObject(InputStream source) throws Exception;

   /**
    * Deserializes a DM Graph container object from XML located at the specified URL.
    */
   public SimBionicJava XMLToObject(URL source) throws Exception;
   
   /**
    * Serializes the given DM Graph container object into XML and outputs to the given OutputStream.
    */
   public void ObjectToXML(SimBionicJava obj, OutputStream output) throws Exception;

   
   /**
    * Serializes the given DM Graph container object into XML and writes to the given Writer.
    */
   public void ObjectToXML(SimBionicJava obj, Writer output) throws Exception;
   
   public void ObjectToXML(SimBionicJava obj, String fileName) throws Exception;
}
