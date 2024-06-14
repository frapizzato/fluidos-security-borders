//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.01 at 03:05:51 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KubernetesNetworkFilteringCondition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KubernetesNetworkFilteringCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="source" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ResourceSelector"/&gt;
 *         &lt;element name="sourcePort" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="destination" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ResourceSelector"/&gt;
 *         &lt;element name="destinationPort" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="protocolType" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ProtocolType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KubernetesNetworkFilteringCondition", propOrder = {
    "source",
    "sourcePort",
    "destination",
    "destinationPort",
    "protocolType"
})
public class KubernetesNetworkFilteringCondition
    extends ConfigurationCondition
{

    @XmlElement(required = true)
    protected ResourceSelector source;
    @XmlElement(required = true)
    protected String sourcePort;
    @XmlElement(required = true)
    protected ResourceSelector destination;
    @XmlElement(required = true)
    protected String destinationPort;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ProtocolType protocolType;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceSelector }
     *     
     */
    public ResourceSelector getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceSelector }
     *     
     */
    public void setSource(ResourceSelector value) {
        this.source = value;
    }

    /**
     * Gets the value of the sourcePort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourcePort() {
        return sourcePort;
    }

    /**
     * Sets the value of the sourcePort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourcePort(String value) {
        this.sourcePort = value;
    }

    /**
     * Gets the value of the destination property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceSelector }
     *     
     */
    public ResourceSelector getDestination() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceSelector }
     *     
     */
    public void setDestination(ResourceSelector value) {
        this.destination = value;
    }

    /**
     * Gets the value of the destinationPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationPort() {
        return destinationPort;
    }

    /**
     * Sets the value of the destinationPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationPort(String value) {
        this.destinationPort = value;
    }

    /**
     * Gets the value of the protocolType property.
     * 
     * @return
     *     possible object is
     *     {@link ProtocolType }
     *     
     */
    public ProtocolType getProtocolType() {
        return protocolType;
    }

    /**
     * Sets the value of the protocolType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProtocolType }
     *     
     */
    public void setProtocolType(ProtocolType value) {
        this.protocolType = value;
    }

}
