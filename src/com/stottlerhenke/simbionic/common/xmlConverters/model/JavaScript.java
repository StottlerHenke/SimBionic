
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JavaScript complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JavaScript">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="jsFiles" type="{}List<String>"/>
 *         &lt;element name="importedJavaClasses" type="{}List<String>"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JavaScript", propOrder = {

})
public class JavaScript {
	
	private static String CORE_JS_FILE = "coreActionsPredicates/coreActionsPredicates.js";

    @XmlElement(required = true)
    protected List<String> jsFiles = new ArrayList<String>();
    @XmlElement(required = true)
    protected List<String> importedJavaClasses = new ArrayList<String>();

    /**
     * Gets the value of the jsFiles property.
     * 
     * @return
     *     possible object is
     *     {@link List<String> }
     *     
     */
    public List<String> getJsFiles() {
    	if (!jsFiles.contains(CORE_JS_FILE)) {
    		jsFiles.add(CORE_JS_FILE);
    	}
        return jsFiles;
    }

    /**
     * Sets the value of the jsFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<String> }
     *     
     */
    public void setJsFiles(List<String> value) {
        this.jsFiles = value;
        if (!jsFiles.contains(CORE_JS_FILE)) {
    		jsFiles.add(CORE_JS_FILE);
    	}
    }

    /**
     * Gets the value of the importedJavaClasses property.
     * 
     * @return
     *     possible object is
     *     {@link List<String> }
     *     
     */
    public List<String> getImportedJavaClasses() {
        return importedJavaClasses;
    }

    /**
     * Sets the value of the importedJavaClasses property.
     * 
     * @param value
     *     
     */
    public void setImportedJavaClasses(List<String> value) {
        this.importedJavaClasses = value;
    }
    
    /**
     * remove the given class form the importedJavaClasses property
     * @param className
     */
    public void removeImportedJavaClass(String className) {
    	this.importedJavaClasses.remove(className);
    }

}
