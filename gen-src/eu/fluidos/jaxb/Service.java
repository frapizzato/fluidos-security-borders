//
// Questo file Ŕ stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrÓ persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Service complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Service"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ServiceType" minOccurs="0"/&gt;
 *         &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="flavor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="msplID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="domainID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Service", propOrder = {
    "name",
    "type",
    "image",
    "flavor",
    "msplID",
    "domainID"
})
public class Service {

    protected String name;
    protected String type;
    protected String image;
    protected String flavor;
    protected String msplID;
    protected String domainID;
    @XmlAttribute(name = "id")
    protected String id;

    /**
     * Recupera il valore della proprietÓ name.
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
     * Imposta il valore della proprietÓ name.
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
     * Recupera il valore della proprietÓ type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Imposta il valore della proprietÓ type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Recupera il valore della proprietÓ image.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImage() {
        return image;
    }

    /**
     * Imposta il valore della proprietÓ image.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImage(String value) {
        this.image = value;
    }

    /**
     * Recupera il valore della proprietÓ flavor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlavor() {
        return flavor;
    }

    /**
     * Imposta il valore della proprietÓ flavor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlavor(String value) {
        this.flavor = value;
    }

    /**
     * Recupera il valore della proprietÓ msplID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsplID() {
        return msplID;
    }

    /**
     * Imposta il valore della proprietÓ msplID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsplID(String value) {
        this.msplID = value;
    }

    /**
     * Recupera il valore della proprietÓ domainID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainID() {
        return domainID;
    }

    /**
     * Imposta il valore della proprietÓ domainID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainID(String value) {
        this.domainID = value;
    }

    /**
     * Recupera il valore della proprietÓ id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Imposta il valore della proprietÓ id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
