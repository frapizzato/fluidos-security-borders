//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.03.28 alle 11:20:02 AM GMT+01:00 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SecuredService complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
     * Recupera il valore della proprietà service.
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
     * Imposta il valore della proprietà service.
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
     * Recupera il valore della proprietà securityRequirements.
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
     * Imposta il valore della proprietà securityRequirements.
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
     * Recupera il valore della proprietà securityPolicies.
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
     * Imposta il valore della proprietà securityPolicies.
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
