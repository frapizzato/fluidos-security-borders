//
// Questo file � stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Integrity complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Integrity"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TechnologyActionSecurityProperty"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="integrityAlgorithm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="integrityHeader" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="integrityPayload" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Integrity", propOrder = {
    "integrityAlgorithm",
    "integrityHeader",
    "integrityPayload"
})
public class Integrity
    extends TechnologyActionSecurityProperty
{

    protected String integrityAlgorithm;
    protected Boolean integrityHeader;
    protected Boolean integrityPayload;

    /**
     * Recupera il valore della propriet� integrityAlgorithm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntegrityAlgorithm() {
        return integrityAlgorithm;
    }

    /**
     * Imposta il valore della propriet� integrityAlgorithm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntegrityAlgorithm(String value) {
        this.integrityAlgorithm = value;
    }

    /**
     * Recupera il valore della propriet� integrityHeader.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIntegrityHeader() {
        return integrityHeader;
    }

    /**
     * Imposta il valore della propriet� integrityHeader.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIntegrityHeader(Boolean value) {
        this.integrityHeader = value;
    }

    /**
     * Recupera il valore della propriet� integrityPayload.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIntegrityPayload() {
        return integrityPayload;
    }

    /**
     * Imposta il valore della propriet� integrityPayload.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIntegrityPayload(Boolean value) {
        this.integrityPayload = value;
    }

}
