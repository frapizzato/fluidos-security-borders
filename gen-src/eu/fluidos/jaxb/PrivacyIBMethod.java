//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.09.10 alle 04:35:03 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per PrivacyIBMethod complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PrivacyIBMethod"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PrivacyMethod"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IB" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrivacyIBMethod", propOrder = {
    "ib"
})
public class PrivacyIBMethod
    extends PrivacyMethod
{

    @XmlElement(name = "IB", required = true)
    protected String ib;

    /**
     * Recupera il valore della proprietà ib.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIB() {
        return ib;
    }

    /**
     * Imposta il valore della proprietà ib.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIB(String value) {
        this.ib = value;
    }

}
