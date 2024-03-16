//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.03.14 at 09:34:48 AM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SecuredService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SecuredService"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="service" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}Service"/&gt;
 *         &lt;element name="securityRequirements" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}SSLALink" minOccurs="0"/&gt;
 *         &lt;element name="securityPolicies" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}MSPLLink" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SecuredService", propOrder = {
    "service",
    "securityRequirements",
    "securityPolicies"
})
public class SecuredService {

    @XmlElement(required = true)
    protected Service service;
    protected SSLALink securityRequirements;
    protected MSPLLink securityPolicies;

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link Service }
     *     
     */
    public Service getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link Service }
     *     
     */
    public void setService(Service value) {
        this.service = value;
    }

    /**
     * Gets the value of the securityRequirements property.
     * 
     * @return
     *     possible object is
     *     {@link SSLALink }
     *     
     */
    public SSLALink getSecurityRequirements() {
        return securityRequirements;
    }

    /**
     * Sets the value of the securityRequirements property.
     * 
     * @param value
     *     allowed object is
     *     {@link SSLALink }
     *     
     */
    public void setSecurityRequirements(SSLALink value) {
        this.securityRequirements = value;
    }

    /**
     * Gets the value of the securityPolicies property.
     * 
     * @return
     *     possible object is
     *     {@link MSPLLink }
     *     
     */
    public MSPLLink getSecurityPolicies() {
        return securityPolicies;
    }

    /**
     * Sets the value of the securityPolicies property.
     * 
     * @param value
     *     allowed object is
     *     {@link MSPLLink }
     *     
     */
    public void setSecurityPolicies(MSPLLink value) {
        this.securityPolicies = value;
    }

}
