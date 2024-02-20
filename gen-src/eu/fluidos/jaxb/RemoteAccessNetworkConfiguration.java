//
// Questo file Ŕ stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrÓ persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RemoteAccessNetworkConfiguration complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
     * Recupera il valore della proprietÓ startIPAddress.
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
     * Imposta il valore della proprietÓ startIPAddress.
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
     * Recupera il valore della proprietÓ maxClients.
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
     * Imposta il valore della proprietÓ maxClients.
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
     * Recupera il valore della proprietÓ netmask.
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
     * Imposta il valore della proprietÓ netmask.
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
     * Recupera il valore della proprietÓ routedSubnets.
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
     * Imposta il valore della proprietÓ routedSubnets.
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
     * Recupera il valore della proprietÓ dnsServer.
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
     * Imposta il valore della proprietÓ dnsServer.
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
     * Recupera il valore della proprietÓ domainSuffix.
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
     * Imposta il valore della proprietÓ domainSuffix.
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
     * Recupera il valore della proprietÓ wins.
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
     * Imposta il valore della proprietÓ wins.
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
     * Recupera il valore della proprietÓ localSubnet.
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
     * Imposta il valore della proprietÓ localSubnet.
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
     * Recupera il valore della proprietÓ remoteSubnet.
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
     * Imposta il valore della proprietÓ remoteSubnet.
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
