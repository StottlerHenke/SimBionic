package com.stottlerhenke.simbionic.common.xmlConverters.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * XXX: 2018-05-03 -jmm copy-paste port of BehaviorFolderGroup
 * for use with Constant objects.
 * 
 * <p>Java class for ConstantFolderGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 * 
 * <pre>
 * &lt;complexType name="ConstantFolderGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="constant" type="{}Constant"/>
 *         &lt;element name="constantFolder" type="{}ConstantFolder"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConstantFolderGroup", propOrder = {
    "constantOrConstantFolder"
})
public class ConstantFolderGroup {

    /**
     * XXX: 2018-05-03 -jmm <br>
     * As far as I can tell, the only reason why this list is
     * {@code List<Object>} is because Java does not support union types in
     * generics; a separate class might not be strictly necessary, but is
     * provided to communicate {@code constantOrConstantFolder} should make
     * sure that elements should be one of the two types (Contrast with the
     * approach before folders, where a "bare" {@code List<Constant>} was used
     * by the {@code SimBionicJava} model instead of an instance of a custom
     * class like ConstantFolderGroup.)
     */
    @XmlElements({
            @XmlElement(name = "constantFolder", type = ConstantFolder.class),
            @XmlElement(name = "constant", type = Constant.class) })
    protected List<Object> constantOrConstantFolder = new ArrayList<Object>();

    /**
     * Gets the value of the constantOrConstantFolder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the constantOrConstantFolder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getConstantOrConstantFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstantFolder } {@link Constant }
     * 
     * 
     */
    public List<Object> getConstantOrConstantFolder() {
        return constantOrConstantFolder;
    }

    public void addConstant(Constant constant) {
        constantOrConstantFolder.add(constant);
    }

    public void removeConstant(Constant constant) {
        constantOrConstantFolder.remove(constant);
    }

    public void addConstantFolder(ConstantFolder constantFolder) {
        constantOrConstantFolder.add(constantFolder);
    }

    public void removeConstantFolder(ConstantFolder constantFolder) {
        constantOrConstantFolder.remove(constantFolder);
    }

}
