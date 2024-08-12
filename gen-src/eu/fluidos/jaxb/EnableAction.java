//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.08.11 alle 11:12:47 AM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per EnableAction complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="EnableAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EnableActionType" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}EnableActionType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnableAction", propOrder = {
    "enableActionType"
})
@XmlSeeAlso({
    ParentalControlAction.class,
    AnonimityAction.class
})
public class EnableAction
    extends ConfigurationAction
{

    @XmlElement(name = "EnableActionType", required = true)
    protected EnableActionType enableActionType;

    /**
     * Recupera il valore della proprietà enableActionType.
     * 
     * @return
     *     possible object is
     *     {@link EnableActionType }
     *     
     */
    public EnableActionType getEnableActionType() {
        return enableActionType;
    }

    /**
     * Imposta il valore della proprietà enableActionType.
     * 
     * @param value
     *     allowed object is
     *     {@link EnableActionType }
     *     
     */
    public void setEnableActionType(EnableActionType value) {
        this.enableActionType = value;
    }

}
