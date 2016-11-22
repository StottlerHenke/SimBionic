
package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PredicateFolderGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PredicateFolderGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="predicate" type="{}Predicate"/>
 *         &lt;element name="predicateFolder" type="{}PredicateFolder"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PredicateFolderGroup", propOrder = {
    "predicateOrPredicateFolder"
})
public class PredicateFolderGroup {

    @XmlElements({
        @XmlElement(name = "predicate", type = Predicate.class),
        @XmlElement(name = "predicateFolder", type = PredicateFolder.class)
    })
    protected List<Object> predicateOrPredicateFolder = new ArrayList<Object>();

    /**
     * Gets the value of the predicateOrPredicateFolder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the predicateOrPredicateFolder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPredicateOrPredicateFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Predicate }
     * {@link PredicateFolder }
     * 
     * 
     */
    public List<Object> getPredicateOrPredicateFolder() {
        return predicateOrPredicateFolder;
    }
    
    public void addPredicate(Predicate predicate) {
       predicateOrPredicateFolder.add(predicate);
    }
    
    public void removePredicate(Predicate predicate) {
       predicateOrPredicateFolder.remove(predicate);
    }
    
    public void addPredicateFolder(PredicateFolder predicateFolder) {
       predicateOrPredicateFolder.add(predicateFolder);
    }
    
    public void removePredicateFolder(PredicateFolder predicateFolder) {
       predicateOrPredicateFolder.remove(predicateFolder);
    }

    public int size() {
    	return predicateOrPredicateFolder.size();
    }
}
