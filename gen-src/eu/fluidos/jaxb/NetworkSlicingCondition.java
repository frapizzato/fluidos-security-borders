//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.03.14 at 09:34:48 AM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NetworkSlicingCondition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NetworkSlicingCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="qosCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}QoSCondition" minOccurs="0"/&gt;
 *         &lt;element name="packetFilterCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PacketFilterCondition" minOccurs="0"/&gt;
 *         &lt;element name="networkSlicingConditionParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}NetworkSlicingConditionParameters" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkSlicingCondition", propOrder = {
    "qosCondition",
    "packetFilterCondition",
    "networkSlicingConditionParameters"
})
@XmlSeeAlso({
    NetworkSlicingConfigurationCondition.class
})
public class NetworkSlicingCondition
    extends ConfigurationCondition
{

    protected QoSCondition qosCondition;
    protected PacketFilterCondition packetFilterCondition;
    protected NetworkSlicingConditionParameters networkSlicingConditionParameters;

    /**
     * Gets the value of the qosCondition property.
     * 
     * @return
     *     possible object is
     *     {@link QoSCondition }
     *     
     */
    public QoSCondition getQosCondition() {
        return qosCondition;
    }

    /**
     * Sets the value of the qosCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link QoSCondition }
     *     
     */
    public void setQosCondition(QoSCondition value) {
        this.qosCondition = value;
    }

    /**
     * Gets the value of the packetFilterCondition property.
     * 
     * @return
     *     possible object is
     *     {@link PacketFilterCondition }
     *     
     */
    public PacketFilterCondition getPacketFilterCondition() {
        return packetFilterCondition;
    }

    /**
     * Sets the value of the packetFilterCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketFilterCondition }
     *     
     */
    public void setPacketFilterCondition(PacketFilterCondition value) {
        this.packetFilterCondition = value;
    }

    /**
     * Gets the value of the networkSlicingConditionParameters property.
     * 
     * @return
     *     possible object is
     *     {@link NetworkSlicingConditionParameters }
     *     
     */
    public NetworkSlicingConditionParameters getNetworkSlicingConditionParameters() {
        return networkSlicingConditionParameters;
    }

    /**
     * Sets the value of the networkSlicingConditionParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkSlicingConditionParameters }
     *     
     */
    public void setNetworkSlicingConditionParameters(NetworkSlicingConditionParameters value) {
        this.networkSlicingConditionParameters = value;
    }

}
