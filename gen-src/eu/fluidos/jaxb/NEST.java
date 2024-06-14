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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NEST complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NEST"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="availability" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="mmtel_support" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="session_and_service_continuity_support" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="three_gpp_5g_qos" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NEST", propOrder = {
    "availability",
    "mmtelSupport",
    "sessionAndServiceContinuitySupport",
    "threeGpp5GQos"
})
public class NEST {

    protected Float availability;
    @XmlElement(name = "mmtel_support")
    protected Integer mmtelSupport;
    @XmlElement(name = "session_and_service_continuity_support")
    protected Integer sessionAndServiceContinuitySupport;
    @XmlElement(name = "three_gpp_5g_qos")
    protected Integer threeGpp5GQos;

    /**
     * Gets the value of the availability property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getAvailability() {
        return availability;
    }

    /**
     * Sets the value of the availability property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setAvailability(Float value) {
        this.availability = value;
    }

    /**
     * Gets the value of the mmtelSupport property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMmtelSupport() {
        return mmtelSupport;
    }

    /**
     * Sets the value of the mmtelSupport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMmtelSupport(Integer value) {
        this.mmtelSupport = value;
    }

    /**
     * Gets the value of the sessionAndServiceContinuitySupport property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSessionAndServiceContinuitySupport() {
        return sessionAndServiceContinuitySupport;
    }

    /**
     * Sets the value of the sessionAndServiceContinuitySupport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSessionAndServiceContinuitySupport(Integer value) {
        this.sessionAndServiceContinuitySupport = value;
    }

    /**
     * Gets the value of the threeGpp5GQos property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getThreeGpp5GQos() {
        return threeGpp5GQos;
    }

    /**
     * Sets the value of the threeGpp5GQos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setThreeGpp5GQos(Integer value) {
        this.threeGpp5GQos = value;
    }

}
