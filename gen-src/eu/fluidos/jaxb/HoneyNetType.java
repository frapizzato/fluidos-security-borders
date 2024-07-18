//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.07.08 alle 07:04:29 PM CEST 
//


package eu.fluidos.jaxb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per honeyNetType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="honeyNetType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="net" type="{http://www.example.org/IoTHoneynetSchema}netType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="router" type="{http://www.example.org/IoTHoneynetSchema}routerType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="containmentGateway" type="{http://www.example.org/IoTHoneynetSchema}containmentGatewayType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="honeyPot" type="{http://www.example.org/IoTHoneynetSchema}honeyPotType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "honeyNetType", namespace = "http://www.example.org/IoTHoneynetSchema", propOrder = {
    "name",
    "net",
    "router",
    "containmentGateway",
    "honeyPot"
})
public class HoneyNetType {

    @XmlElement(required = true)
    protected String name;
    protected List<NetType> net;
    protected List<RouterType> router;
    protected List<ContainmentGatewayType> containmentGateway;
    protected List<HoneyPotType> honeyPot;

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the net property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the net property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NetType }
     * 
     * 
     */
    public List<NetType> getNet() {
        if (net == null) {
            net = new ArrayList<NetType>();
        }
        return this.net;
    }

    /**
     * Gets the value of the router property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the router property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRouter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RouterType }
     * 
     * 
     */
    public List<RouterType> getRouter() {
        if (router == null) {
            router = new ArrayList<RouterType>();
        }
        return this.router;
    }

    /**
     * Gets the value of the containmentGateway property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the containmentGateway property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContainmentGateway().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContainmentGatewayType }
     * 
     * 
     */
    public List<ContainmentGatewayType> getContainmentGateway() {
        if (containmentGateway == null) {
            containmentGateway = new ArrayList<ContainmentGatewayType>();
        }
        return this.containmentGateway;
    }

    /**
     * Gets the value of the honeyPot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the honeyPot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHoneyPot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HoneyPotType }
     * 
     * 
     */
    public List<HoneyPotType> getHoneyPot() {
        if (honeyPot == null) {
            honeyPot = new ArrayList<HoneyPotType>();
        }
        return this.honeyPot;
    }

}
