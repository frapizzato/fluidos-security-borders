//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.08.21 alle 04:24:06 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per LoggingAction complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LoggingAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="loggingActionType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoggingAction", propOrder = {
    "loggingActionType"
})
public class LoggingAction
    extends ConfigurationAction
{

    @XmlElement(required = true)
    protected String loggingActionType;

    /**
     * Recupera il valore della proprietà loggingActionType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoggingActionType() {
        return loggingActionType;
    }

    /**
     * Imposta il valore della proprietà loggingActionType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoggingActionType(String value) {
        this.loggingActionType = value;
    }

}
