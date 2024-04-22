//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.03.14 at 09:34:48 AM CET 
//


package eu.fluidos.jaxb;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OnionRoutingTechnologyParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OnionRoutingTechnologyParameter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AnonymityTechnologyParameter"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="exitRelay" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="IPv6Exit" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="publicRelay" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="hiddenServiceDir" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hiddenServicePort" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="relayBandwidthRate" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="relayBandwidthBurst" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OnionRoutingTechnologyParameter", propOrder = {
    "exitRelay",
    "iPv6Exit",
    "publicRelay",
    "hiddenServiceDir",
    "hiddenServicePort",
    "relayBandwidthRate",
    "relayBandwidthBurst"
})
public class OnionRoutingTechnologyParameter
    extends AnonymityTechnologyParameter
{

    protected boolean exitRelay;
    @XmlElement(name = "IPv6Exit")
    protected boolean iPv6Exit;
    protected boolean publicRelay;
    @XmlElement(required = true)
    protected String hiddenServiceDir;
    @XmlElement(required = true)
    protected String hiddenServicePort;
    @XmlElement(required = true)
    protected BigInteger relayBandwidthRate;
    @XmlElement(required = true)
    protected BigInteger relayBandwidthBurst;

    /**
     * Gets the value of the exitRelay property.
     * 
     */
    public boolean isExitRelay() {
        return exitRelay;
    }

    /**
     * Sets the value of the exitRelay property.
     * 
     */
    public void setExitRelay(boolean value) {
        this.exitRelay = value;
    }

    /**
     * Gets the value of the iPv6Exit property.
     * 
     */
    public boolean isIPv6Exit() {
        return iPv6Exit;
    }

    /**
     * Sets the value of the iPv6Exit property.
     * 
     */
    public void setIPv6Exit(boolean value) {
        this.iPv6Exit = value;
    }

    /**
     * Gets the value of the publicRelay property.
     * 
     */
    public boolean isPublicRelay() {
        return publicRelay;
    }

    /**
     * Sets the value of the publicRelay property.
     * 
     */
    public void setPublicRelay(boolean value) {
        this.publicRelay = value;
    }

    /**
     * Gets the value of the hiddenServiceDir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHiddenServiceDir() {
        return hiddenServiceDir;
    }

    /**
     * Sets the value of the hiddenServiceDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHiddenServiceDir(String value) {
        this.hiddenServiceDir = value;
    }

    /**
     * Gets the value of the hiddenServicePort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHiddenServicePort() {
        return hiddenServicePort;
    }

    /**
     * Sets the value of the hiddenServicePort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHiddenServicePort(String value) {
        this.hiddenServicePort = value;
    }

    /**
     * Gets the value of the relayBandwidthRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRelayBandwidthRate() {
        return relayBandwidthRate;
    }

    /**
     * Sets the value of the relayBandwidthRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRelayBandwidthRate(BigInteger value) {
        this.relayBandwidthRate = value;
    }

    /**
     * Gets the value of the relayBandwidthBurst property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRelayBandwidthBurst() {
        return relayBandwidthBurst;
    }

    /**
     * Sets the value of the relayBandwidthBurst property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRelayBandwidthBurst(BigInteger value) {
        this.relayBandwidthBurst = value;
    }

}