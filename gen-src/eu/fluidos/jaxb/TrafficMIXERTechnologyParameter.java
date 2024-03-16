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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrafficMIXERTechnologyParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrafficMIXERTechnologyParameter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TechnologySpecificParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GW" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="peerID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cryptoBackend" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrafficMIXERTechnologyParameter", propOrder = {
    "gw",
    "peerID",
    "key",
    "cryptoBackend"
})
public class TrafficMIXERTechnologyParameter
    extends TechnologySpecificParameters
{

    @XmlElement(name = "GW", required = true)
    protected String gw;
    @XmlElement(required = true)
    protected String peerID;
    @XmlElement(required = true)
    protected String key;
    @XmlElement(required = true)
    protected String cryptoBackend;

    /**
     * Gets the value of the gw property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGW() {
        return gw;
    }

    /**
     * Sets the value of the gw property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGW(String value) {
        this.gw = value;
    }

    /**
     * Gets the value of the peerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeerID() {
        return peerID;
    }

    /**
     * Sets the value of the peerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeerID(String value) {
        this.peerID = value;
    }

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

    /**
     * Gets the value of the cryptoBackend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCryptoBackend() {
        return cryptoBackend;
    }

    /**
     * Sets the value of the cryptoBackend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCryptoBackend(String value) {
        this.cryptoBackend = value;
    }

}
