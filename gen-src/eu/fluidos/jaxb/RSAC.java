//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.03.14 at 09:34:48 AM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RSAC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RSAC"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="RSACviolence"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="RSACsex"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="RSACnudity"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="RSAClanguage"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RSAC")
public class RSAC {

    @XmlAttribute(name = "RSACviolence")
    protected Integer rsaCviolence;
    @XmlAttribute(name = "RSACsex")
    protected Integer rsaCsex;
    @XmlAttribute(name = "RSACnudity")
    protected Integer rsaCnudity;
    @XmlAttribute(name = "RSAClanguage")
    protected Integer rsaClanguage;

    /**
     * Gets the value of the rsaCviolence property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRSACviolence() {
        return rsaCviolence;
    }

    /**
     * Sets the value of the rsaCviolence property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRSACviolence(Integer value) {
        this.rsaCviolence = value;
    }

    /**
     * Gets the value of the rsaCsex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRSACsex() {
        return rsaCsex;
    }

    /**
     * Sets the value of the rsaCsex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRSACsex(Integer value) {
        this.rsaCsex = value;
    }

    /**
     * Gets the value of the rsaCnudity property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRSACnudity() {
        return rsaCnudity;
    }

    /**
     * Sets the value of the rsaCnudity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRSACnudity(Integer value) {
        this.rsaCnudity = value;
    }

    /**
     * Gets the value of the rsaClanguage property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRSAClanguage() {
        return rsaClanguage;
    }

    /**
     * Sets the value of the rsaClanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRSAClanguage(Integer value) {
        this.rsaClanguage = value;
    }

}