//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.08.11 alle 11:12:47 AM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Vancouver complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Vancouver"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="Vancouvereducationalcontent"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="-3"/&gt;
 *             &lt;maxInclusive value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Vancouverviolence"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="-3"/&gt;
 *             &lt;maxInclusive value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Vancouversex"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="-3"/&gt;
 *             &lt;maxInclusive value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Vancouverprofanity"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="-3"/&gt;
 *             &lt;maxInclusive value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Vancouvergambling"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="-3"/&gt;
 *             &lt;maxInclusive value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Vancouver")
public class Vancouver {

    @XmlAttribute(name = "Vancouvereducationalcontent")
    protected Integer vancouvereducationalcontent;
    @XmlAttribute(name = "Vancouverviolence")
    protected Integer vancouverviolence;
    @XmlAttribute(name = "Vancouversex")
    protected Integer vancouversex;
    @XmlAttribute(name = "Vancouverprofanity")
    protected Integer vancouverprofanity;
    @XmlAttribute(name = "Vancouvergambling")
    protected Integer vancouvergambling;

    /**
     * Recupera il valore della proprietà vancouvereducationalcontent.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVancouvereducationalcontent() {
        return vancouvereducationalcontent;
    }

    /**
     * Imposta il valore della proprietà vancouvereducationalcontent.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVancouvereducationalcontent(Integer value) {
        this.vancouvereducationalcontent = value;
    }

    /**
     * Recupera il valore della proprietà vancouverviolence.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVancouverviolence() {
        return vancouverviolence;
    }

    /**
     * Imposta il valore della proprietà vancouverviolence.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVancouverviolence(Integer value) {
        this.vancouverviolence = value;
    }

    /**
     * Recupera il valore della proprietà vancouversex.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVancouversex() {
        return vancouversex;
    }

    /**
     * Imposta il valore della proprietà vancouversex.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVancouversex(Integer value) {
        this.vancouversex = value;
    }

    /**
     * Recupera il valore della proprietà vancouverprofanity.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVancouverprofanity() {
        return vancouverprofanity;
    }

    /**
     * Imposta il valore della proprietà vancouverprofanity.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVancouverprofanity(Integer value) {
        this.vancouverprofanity = value;
    }

    /**
     * Recupera il valore della proprietà vancouvergambling.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVancouvergambling() {
        return vancouvergambling;
    }

    /**
     * Imposta il valore della proprietà vancouvergambling.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVancouvergambling(Integer value) {
        this.vancouvergambling = value;
    }

}
