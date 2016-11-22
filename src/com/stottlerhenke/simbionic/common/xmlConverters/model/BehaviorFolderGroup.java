
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BehaviorFolderGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BehaviorFolderGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="behavior" type="{}Behavior"/>
 *         &lt;element name="behaviorFolder" type="{}BehaviorFolder"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BehaviorFolderGroup", propOrder = {
    "behaviorOrBehaviorFolder"
})
public class BehaviorFolderGroup {

    @XmlElements({
        @XmlElement(name = "behaviorFolder", type = BehaviorFolder.class),
        @XmlElement(name = "behavior", type = Behavior.class)
    })
    protected List<Object> behaviorOrBehaviorFolder = new ArrayList<Object>();

    /**
     * Gets the value of the behaviorOrBehaviorFolder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the behaviorOrBehaviorFolder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBehaviorOrBehaviorFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BehaviorFolder }
     * {@link Behavior }
     * 
     * 
     */
    public List<Object> getBehaviorOrBehaviorFolder() {
        return behaviorOrBehaviorFolder;
    }
    
    public void addBehavior(Behavior behavior) {
       behaviorOrBehaviorFolder.add(behavior);
    }
    
    public void removeBehavior(Behavior behavior) {
       behaviorOrBehaviorFolder.remove(behavior);
    }
    
    public void addBehaviorFolder(BehaviorFolder behaviorFolder) {
       behaviorOrBehaviorFolder.add(behaviorFolder);
    }
    
    public void removeBehaviorFolder(BehaviorFolder behaviorFolder) {
       behaviorOrBehaviorFolder.remove(behaviorFolder);
    }


}
