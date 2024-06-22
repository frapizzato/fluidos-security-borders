//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.06.22 alle 12:21:07 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per AuthorizationAction complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AuthorizationAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AuthorizationActionType" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AuthorizationActionType"/&gt;
 *         &lt;element name="AuthorizationSubject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AuthorizationTarget" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationAction", propOrder = {
    "authorizationActionType",
    "authorizationSubject",
    "authorizationTarget"
})
public class AuthorizationAction
    extends ConfigurationAction
{

    @XmlElement(name = "AuthorizationActionType", required = true)
    @XmlSchemaType(name = "string")
    protected AuthorizationActionType authorizationActionType;
    @XmlElement(name = "AuthorizationSubject")
    protected String authorizationSubject;
    @XmlElement(name = "AuthorizationTarget")
    protected String authorizationTarget;

    /**
     * Recupera il valore della proprietà authorizationActionType.
     * 
     * @return
     *     possible object is
     *     {@link AuthorizationActionType }
     *     
     */
    public AuthorizationActionType getAuthorizationActionType() {
        return authorizationActionType;
    }

    /**
     * Imposta il valore della proprietà authorizationActionType.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthorizationActionType }
     *     
     */
    public void setAuthorizationActionType(AuthorizationActionType value) {
        this.authorizationActionType = value;
    }

    /**
     * Recupera il valore della proprietà authorizationSubject.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationSubject() {
        return authorizationSubject;
    }

    /**
     * Imposta il valore della proprietà authorizationSubject.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationSubject(String value) {
        this.authorizationSubject = value;
    }

    /**
     * Recupera il valore della proprietà authorizationTarget.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationTarget() {
        return authorizationTarget;
    }

    /**
     * Imposta il valore della proprietà authorizationTarget.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationTarget(String value) {
        this.authorizationTarget = value;
    }

}
