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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnableAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the enableActionType property.
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
     * Sets the value of the enableActionType property.
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
