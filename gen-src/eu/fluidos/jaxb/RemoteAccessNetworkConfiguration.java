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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RemoteAccessNetworkConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RemoteAccessNetworkConfiguration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AdditionalNetworkConfigurationParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="startIPAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="maxClients" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="netmask" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="routedSubnets" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dnsServer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="domainSuffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="wins" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="localSubnet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="remoteSubnet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemoteAccessNetworkConfiguration", propOrder = {
    "startIPAddress",
    "maxClients",
    "netmask",
    "routedSubnets",
    "dnsServer",
    "domainSuffix",
    "wins",
    "localSubnet",
    "remoteSubnet"
})
public class RemoteAccessNetworkConfiguration
    extends AdditionalNetworkConfigurationParameters
{

    protected String startIPAddress;
    protected BigInteger maxClients;
    protected String netmask;
    protected String routedSubnets;
    protected String dnsServer;
    protected String domainSuffix;
    protected String wins;
    protected String localSubnet;
    protected String remoteSubnet;

    /**
     * Gets the value of the startIPAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartIPAddress() {
        return startIPAddress;
    }

    /**
     * Sets the value of the startIPAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartIPAddress(String value) {
        this.startIPAddress = value;
    }

    /**
     * Gets the value of the maxClients property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxClients() {
        return maxClients;
    }

    /**
     * Sets the value of the maxClients property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxClients(BigInteger value) {
        this.maxClients = value;
    }

    /**
     * Gets the value of the netmask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetmask() {
        return netmask;
    }

    /**
     * Sets the value of the netmask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetmask(String value) {
        this.netmask = value;
    }

    /**
     * Gets the value of the routedSubnets property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoutedSubnets() {
        return routedSubnets;
    }

    /**
     * Sets the value of the routedSubnets property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoutedSubnets(String value) {
        this.routedSubnets = value;
    }

    /**
     * Gets the value of the dnsServer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDnsServer() {
        return dnsServer;
    }

    /**
     * Sets the value of the dnsServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDnsServer(String value) {
        this.dnsServer = value;
    }

    /**
     * Gets the value of the domainSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainSuffix() {
        return domainSuffix;
    }

    /**
     * Sets the value of the domainSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainSuffix(String value) {
        this.domainSuffix = value;
    }

    /**
     * Gets the value of the wins property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWins() {
        return wins;
    }

    /**
     * Sets the value of the wins property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWins(String value) {
        this.wins = value;
    }

    /**
     * Gets the value of the localSubnet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalSubnet() {
        return localSubnet;
    }

    /**
     * Sets the value of the localSubnet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalSubnet(String value) {
        this.localSubnet = value;
    }

    /**
     * Gets the value of the remoteSubnet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteSubnet() {
        return remoteSubnet;
    }

    /**
     * Sets the value of the remoteSubnet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteSubnet(String value) {
        this.remoteSubnet = value;
    }

}
