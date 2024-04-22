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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VIoTHoneyNetAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the vIoTHoneyNetActionType property.
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
     * Sets the value of the vIoTHoneyNetActionType property.
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
     * Gets the value of the ioTHoneyNet property.
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
     * Sets the value of the ioTHoneyNet property.
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