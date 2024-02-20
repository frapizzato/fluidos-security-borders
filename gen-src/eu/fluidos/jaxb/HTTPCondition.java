//
// Questo file Ŕ stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrÓ persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per HTTPCondition complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="HTTPCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="httpMetod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="browser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="user_cert" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ca_cert" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="request_mime_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="response_mime_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="http_regex_header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="http_status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HTTPCondition", propOrder = {
    "httpMetod",
    "browser",
    "userCert",
    "caCert",
    "requestMimeType",
    "responseMimeType",
    "httpRegexHeader",
    "httpStatus"
})
public class HTTPCondition {

    protected String httpMetod;
    protected String browser;
    @XmlElement(name = "user_cert")
    protected String userCert;
    @XmlElement(name = "ca_cert")
    protected String caCert;
    @XmlElement(name = "request_mime_type")
    protected String requestMimeType;
    @XmlElement(name = "response_mime_type")
    protected String responseMimeType;
    @XmlElement(name = "http_regex_header")
    protected String httpRegexHeader;
    @XmlElement(name = "http_status")
    protected String httpStatus;

    /**
     * Recupera il valore della proprietÓ httpMetod.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHttpMetod() {
        return httpMetod;
    }

    /**
     * Imposta il valore della proprietÓ httpMetod.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHttpMetod(String value) {
        this.httpMetod = value;
    }

    /**
     * Recupera il valore della proprietÓ browser.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * Imposta il valore della proprietÓ browser.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowser(String value) {
        this.browser = value;
    }

    /**
     * Recupera il valore della proprietÓ userCert.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserCert() {
        return userCert;
    }

    /**
     * Imposta il valore della proprietÓ userCert.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserCert(String value) {
        this.userCert = value;
    }

    /**
     * Recupera il valore della proprietÓ caCert.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaCert() {
        return caCert;
    }

    /**
     * Imposta il valore della proprietÓ caCert.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaCert(String value) {
        this.caCert = value;
    }

    /**
     * Recupera il valore della proprietÓ requestMimeType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestMimeType() {
        return requestMimeType;
    }

    /**
     * Imposta il valore della proprietÓ requestMimeType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestMimeType(String value) {
        this.requestMimeType = value;
    }

    /**
     * Recupera il valore della proprietÓ responseMimeType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseMimeType() {
        return responseMimeType;
    }

    /**
     * Imposta il valore della proprietÓ responseMimeType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseMimeType(String value) {
        this.responseMimeType = value;
    }

    /**
     * Recupera il valore della proprietÓ httpRegexHeader.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHttpRegexHeader() {
        return httpRegexHeader;
    }

    /**
     * Imposta il valore della proprietÓ httpRegexHeader.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHttpRegexHeader(String value) {
        this.httpRegexHeader = value;
    }

    /**
     * Recupera il valore della proprietÓ httpStatus.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHttpStatus() {
        return httpStatus;
    }

    /**
     * Imposta il valore della proprietÓ httpStatus.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHttpStatus(String value) {
        this.httpStatus = value;
    }

}
