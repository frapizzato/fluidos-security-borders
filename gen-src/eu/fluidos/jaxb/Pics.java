//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.01 at 03:05:51 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Pics complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Pics"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ICRA" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ICRA"/&gt;
 *         &lt;element name="RSAC" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}RSAC"/&gt;
 *         &lt;element name="evaluWEB"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *               &lt;maxInclusive value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CyberNOTsex"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *               &lt;maxInclusive value="8"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Weburbia"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *               &lt;maxInclusive value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Vancouver" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}Vancouver"/&gt;
 *         &lt;element name="SafeNet" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}SafeNet"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Pics", propOrder = {
    "icra",
    "rsac",
    "evaluWEB",
    "cyberNOTsex",
    "weburbia",
    "vancouver",
    "safeNet"
})
public class Pics {

    @XmlElement(name = "ICRA", required = true)
    protected ICRA icra;
    @XmlElement(name = "RSAC", required = true)
    protected RSAC rsac;
    protected int evaluWEB;
    @XmlElement(name = "CyberNOTsex")
    protected int cyberNOTsex;
    @XmlElement(name = "Weburbia")
    protected int weburbia;
    @XmlElement(name = "Vancouver", required = true)
    protected Vancouver vancouver;
    @XmlElement(name = "SafeNet", required = true)
    protected SafeNet safeNet;

    /**
     * Gets the value of the icra property.
     * 
     * @return
     *     possible object is
     *     {@link ICRA }
     *     
     */
    public ICRA getICRA() {
        return icra;
    }

    /**
     * Sets the value of the icra property.
     * 
     * @param value
     *     allowed object is
     *     {@link ICRA }
     *     
     */
    public void setICRA(ICRA value) {
        this.icra = value;
    }

    /**
     * Gets the value of the rsac property.
     * 
     * @return
     *     possible object is
     *     {@link RSAC }
     *     
     */
    public RSAC getRSAC() {
        return rsac;
    }

    /**
     * Sets the value of the rsac property.
     * 
     * @param value
     *     allowed object is
     *     {@link RSAC }
     *     
     */
    public void setRSAC(RSAC value) {
        this.rsac = value;
    }

    /**
     * Gets the value of the evaluWEB property.
     * 
     */
    public int getEvaluWEB() {
        return evaluWEB;
    }

    /**
     * Sets the value of the evaluWEB property.
     * 
     */
    public void setEvaluWEB(int value) {
        this.evaluWEB = value;
    }

    /**
     * Gets the value of the cyberNOTsex property.
     * 
     */
    public int getCyberNOTsex() {
        return cyberNOTsex;
    }

    /**
     * Sets the value of the cyberNOTsex property.
     * 
     */
    public void setCyberNOTsex(int value) {
        this.cyberNOTsex = value;
    }

    /**
     * Gets the value of the weburbia property.
     * 
     */
    public int getWeburbia() {
        return weburbia;
    }

    /**
     * Sets the value of the weburbia property.
     * 
     */
    public void setWeburbia(int value) {
        this.weburbia = value;
    }

    /**
     * Gets the value of the vancouver property.
     * 
     * @return
     *     possible object is
     *     {@link Vancouver }
     *     
     */
    public Vancouver getVancouver() {
        return vancouver;
    }

    /**
     * Sets the value of the vancouver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vancouver }
     *     
     */
    public void setVancouver(Vancouver value) {
        this.vancouver = value;
    }

    /**
     * Gets the value of the safeNet property.
     * 
     * @return
     *     possible object is
     *     {@link SafeNet }
     *     
     */
    public SafeNet getSafeNet() {
        return safeNet;
    }

    /**
     * Sets the value of the safeNet property.
     * 
     * @param value
     *     allowed object is
     *     {@link SafeNet }
     *     
     */
    public void setSafeNet(SafeNet value) {
        this.safeNet = value;
    }

}
