//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.03.14 at 09:34:48 AM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QoSAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QoSAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}Configuration"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="qosAction" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}QoSCondition" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QoSAction", propOrder = {
    "qosAction"
})
public class QoSAction
    extends Configuration
{

    protected QoSCondition qosAction;

    /**
     * Gets the value of the qosAction property.
     * 
     * @return
     *     possible object is
     *     {@link QoSCondition }
     *     
     */
    public QoSCondition getQosAction() {
        return qosAction;
    }

    /**
     * Sets the value of the qosAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link QoSCondition }
     *     
     */
    public void setQosAction(QoSCondition value) {
        this.qosAction = value;
    }

}