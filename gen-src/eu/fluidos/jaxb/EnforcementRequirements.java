//
// Questo file � stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per EnforcementRequirements complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="EnforcementRequirements"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="enforcementDomains" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}EnforcementDomains"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnforcementRequirements", propOrder = {
    "enforcementDomains"
})
public class EnforcementRequirements {

    @XmlElement(required = true)
    protected EnforcementDomains enforcementDomains;

    /**
     * Recupera il valore della propriet� enforcementDomains.
     * 
     * @return
     *     possible object is
     *     {@link EnforcementDomains }
     *     
     */
    public EnforcementDomains getEnforcementDomains() {
        return enforcementDomains;
    }

    /**
     * Imposta il valore della propriet� enforcementDomains.
     * 
     * @param value
     *     allowed object is
     *     {@link EnforcementDomains }
     *     
     */
    public void setEnforcementDomains(EnforcementDomains value) {
        this.enforcementDomains = value;
    }

}