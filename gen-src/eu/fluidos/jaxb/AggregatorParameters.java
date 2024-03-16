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
 * <p>Java class for AggregatorParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AggregatorParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rounds" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="fusionAlgorithm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="apiPort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="minClients" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AggregatorParameters", propOrder = {
    "url",
    "rounds",
    "fusionAlgorithm",
    "apiPort",
    "minClients"
})
public class AggregatorParameters
    extends ConfigurationCondition
{

    @XmlElement(name = "URL")
    protected String url;
    protected BigInteger rounds;
    protected String fusionAlgorithm;
    protected String apiPort;
    protected String minClients;

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURL(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the rounds property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRounds() {
        return rounds;
    }

    /**
     * Sets the value of the rounds property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRounds(BigInteger value) {
        this.rounds = value;
    }

    /**
     * Gets the value of the fusionAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFusionAlgorithm() {
        return fusionAlgorithm;
    }

    /**
     * Sets the value of the fusionAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFusionAlgorithm(String value) {
        this.fusionAlgorithm = value;
    }

    /**
     * Gets the value of the apiPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApiPort() {
        return apiPort;
    }

    /**
     * Sets the value of the apiPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApiPort(String value) {
        this.apiPort = value;
    }

    /**
     * Gets the value of the minClients property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinClients() {
        return minClients;
    }

    /**
     * Sets the value of the minClients property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinClients(String value) {
        this.minClients = value;
    }

}
