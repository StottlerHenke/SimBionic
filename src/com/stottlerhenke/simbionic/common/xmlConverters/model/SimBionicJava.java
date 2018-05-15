
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SimBionicJava complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SimBionicJava">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ipAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="loopBack" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="main" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="actions" type="{}ActionFolderGroup"/>
 *         &lt;element name="predicates" type="{}PredicateFolderGroup"/>
 *         &lt;element name="constants" type="{}List<Constant>"/>
 *         &lt;element name="categories" type="{}List<Category>"/>
 *         &lt;element name="behaviors" type="{}BehaviorFolderGroup"/>
 *         &lt;element name="globals" type="{}List<Global>"/>
 *         &lt;element name="javaScript" type="{}JavaScript"/>
 *         &lt;element name="projectProperties" type="{}ProjectProperties"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimBionicJava", propOrder = {

})
public class SimBionicJava {

    @XmlElement(required = true)
    protected int version;
    @XmlElement(required = true)
    protected String ipAddress;
    protected boolean loopBack;
    @XmlElement(required = true)
    protected String main;
    @XmlElement(required = true)
    protected ActionFolderGroup actions = new ActionFolderGroup();
    @XmlElement(required = true)
    protected PredicateFolderGroup predicates = new PredicateFolderGroup();
    @XmlElement(required = true)
    protected ConstantFolderGroup constants = new ConstantFolderGroup();
    @XmlElement(required = true)
    protected List<Category> categories = new ArrayList<Category>();
    @XmlElement(required = true)
    protected BehaviorFolderGroup behaviors = new BehaviorFolderGroup();
    @XmlElement(required = true)
    protected GlobalFolderGroup globals = new GlobalFolderGroup();
    @XmlElement(required = true)
    protected JavaScript javaScript = new JavaScript();
    
    protected ProjectProperties projectProperties = new ProjectProperties();

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setVersion(int value) {
        this.version = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the loopBack property.
     * 
     */
    public boolean isLoopBack() {
        return loopBack;
    }

    /**
     * Sets the value of the loopBack property.
     * 
     */
    public void setLoopBack(boolean value) {
        this.loopBack = value;
    }

    /**
     * Gets the value of the main property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMain() {
        return main;
    }

    /**
     * Sets the value of the main property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMain(String value) {
        this.main = value;
    }

    /**
     * Gets the value of the actions property.
     * 
     * @return
     *     possible object is
     *     {@link ActionFolderGroup }
     *     
     */
    public ActionFolderGroup getActions() {
        return actions;
    }

    /**
     * Sets the value of the actions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionFolderGroup }
     *     
     */
    public void setActions(ActionFolderGroup value) {
        this.actions = value;
    }

    /**
     * Gets the value of the predicates property.
     * 
     * @return
     *     possible object is
     *     {@link PredicateFolderGroup }
     *     
     */
    public PredicateFolderGroup getPredicates() {
        return predicates;
    }

    /**
     * Sets the value of the predicates property.
     * 
     * @param value
     *     allowed object is
     *     {@link PredicateFolderGroup }
     *     
     */
    public void setPredicates(PredicateFolderGroup value) {
        this.predicates = value;
    }

    /**
     * Gets the value of the constants property.
     * 
     * @return
     *     possible object is
     *     {@link List<Constant> }
     *     
     */
    public ConstantFolderGroup getConstants() {
        return constants;
    }

    /**
     * Sets the value of the constants property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Constant> }
     *     
     */
    public void setConstants(ConstantFolderGroup value) {
        this.constants = value;
    }

    /**
     * Gets the value of the categories property.
     * 
     * @return
     *     possible object is
     *     {@link List<Category> }
     *     
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Sets the value of the categories property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Category> }
     *     
     */
    public void setCategories(List<Category> value) {
        this.categories = value;
    }
    
    public void addCategory(Category category) {
       this.categories.add(category);
    }
    
    public void removeCategory(Category category) {
       this.categories.remove(category);
    }

    /**
     * Gets the value of the behaviors property.
     * 
     * @return
     *     possible object is
     *     {@link BehaviorFolderGroup }
     *     
     */
    public BehaviorFolderGroup getBehaviors() {
        return behaviors;
    }

    /**
     * Sets the value of the behaviors property.
     * 
     * @param value
     *     allowed object is
     *     {@link BehaviorFolderGroup }
     *     
     */
    public void setBehaviors(BehaviorFolderGroup value) {
        this.behaviors = value;
    }
    
    /**
     * Gets the value of the globals property.
     * 
     * @return
     *     possible object is
     *     {@link List<Global> }
     *     
     */
    public GlobalFolderGroup getGlobals() {
        return globals;
    }

    /**
     * Sets the value of the globals property.
     * 
     * @param value
     *     allowed object is
     *     {@link List<Global> }
     *     
     */
    public void setGlobals(GlobalFolderGroup value) {
        this.globals = value;
    }

    /**
     * Gets the value of the javaScript property.
     * 
     * @return
     *     possible object is
     *     {@link JavaScript }
     *     
     */
    public JavaScript getJavaScript() {
        return javaScript;
    }

    /**
     * Sets the value of the javaScript property.
     * 
     * @param value
     *     allowed object is
     *     {@link JavaScript }
     *     
     */
    public void setJavaScript(JavaScript value) {
        this.javaScript = value;
    }

    /**
     * Documentation about the project
     * @return
     */
	public ProjectProperties getProjectProperties() {
		return projectProperties;
	}

	/**
	 * Documentation about the project
	 * @param projectProperties
	 */
	public void setProjectProperties(ProjectProperties projectProperties) {
		this.projectProperties = projectProperties;
	}
    
    

}
