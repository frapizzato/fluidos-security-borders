//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.08.18 alle 07:01:55 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per VIoTHoneyNetAction complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="VIoTHoneyNetAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="VIoTHoneyNetActionType" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}VIoTHoneyNetActionType"/&gt;
 *         &lt;element name="ioTHoneyNet" type="{http://www.example.org/IoTHoneynetSchema}ioTHoneyNetType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VIoTHoneyNetAction", propOrder = {
    "vIoTHoneyNetActionType",
    "ioTHoneyNet"
})
public class VIoTHoneyNetAction
    extends ConfigurationAction
{

    @XmlElement(name = "VIoTHoneyNetActionType", required = true)
    @XmlSchemaType(name = "string")
    protected VIoTHoneyNetActionType vIoTHoneyNetActionType;
    @XmlElement(required = true)
    protected IoTHoneyNetType ioTHoneyNet;

    /**
     * Recupera il valore della proprietà vIoTHoneyNetActionType.
     * 
     * @return
     *     possible object is
     *     {@link VIoTHoneyNetActionType }
     *     
     */
    public VIoTHoneyNetActionType getVIoTHoneyNetActionType() {
        return vIoTHoneyNetActionType;
    }

    /**
     * Imposta il valore della proprietà vIoTHoneyNetActionType.
     * 
     * @param value
     *     allowed object is
     *     {@link VIoTHoneyNetActionType }
     *     
     */
    public void setVIoTHoneyNetActionType(VIoTHoneyNetActionType value) {
        this.vIoTHoneyNetActionType = value;
    }

    /**
     * Recupera il valore della proprietà ioTHoneyNet.
     * 
     * @return
     *     possible object is
     *     {@link IoTHoneyNetType }
     *     
     */
    public IoTHoneyNetType getIoTHoneyNet() {
        return ioTHoneyNet;
    }

    /**
     * Imposta il valore della proprietà ioTHoneyNet.
     * 
     * @param value
     *     allowed object is
     *     {@link IoTHoneyNetType }
     *     
     */
    public void setIoTHoneyNet(IoTHoneyNetType value) {
        this.ioTHoneyNet = value;
    }

}
