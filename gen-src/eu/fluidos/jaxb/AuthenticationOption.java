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
 * <p>Java class for AuthenticationOption complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthenticationOption"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AuthenticationTarget" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PacketFilterCondition"/&gt;
 *         &lt;element name="AuthenticationMethod" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AuthenticationMechanism" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AuthenticationParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AuthenticationParameters"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationOption", propOrder = {
    "authenticationTarget",
    "authenticationMethod",
    "authenticationMechanism",
    "authenticationParameters"
})
public class AuthenticationOption {

    @XmlElement(name = "AuthenticationTarget", required = true)
    protected PacketFilterCondition authenticationTarget;
    @XmlElement(name = "AuthenticationMethod", required = true)
    protected String authenticationMethod;
    @XmlElement(name = "AuthenticationMechanism", required = true)
    protected String authenticationMechanism;
    @XmlElement(name = "AuthenticationParameters", required = true)
    protected AuthenticationParameters authenticationParameters;

    /**
     * Gets the value of the authenticationTarget property.
     * 
     * @return
     *     possible object is
     *     {@link PacketFilterCondition }
     *     
     */
    public PacketFilterCondition getAuthenticationTarget() {
        return authenticationTarget;
    }

    /**
     * Sets the value of the authenticationTarget property.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketFilterCondition }
     *     
     */
    public void setAuthenticationTarget(PacketFilterCondition value) {
        this.authenticationTarget = value;
    }

    /**
     * Gets the value of the authenticationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    /**
     * Sets the value of the authenticationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationMethod(String value) {
        this.authenticationMethod = value;
    }

    /**
     * Gets the value of the authenticationMechanism property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    /**
     * Sets the value of the authenticationMechanism property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationMechanism(String value) {
        this.authenticationMechanism = value;
    }

    /**
     * Gets the value of the authenticationParameters property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationParameters }
     *     
     */
    public AuthenticationParameters getAuthenticationParameters() {
        return authenticationParameters;
    }

    /**
     * Sets the value of the authenticationParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationParameters }
     *     
     */
    public void setAuthenticationParameters(AuthenticationParameters value) {
        this.authenticationParameters = value;
    }

}
