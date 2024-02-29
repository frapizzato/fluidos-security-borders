//
// Questo file � stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per AuthenticationParameters complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AuthenticationParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="psKey_value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="psKey_path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ca_path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cert_path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="publicKey_path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="publicKey_filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="publicKey_passphrase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ca_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ca_filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cert_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cert_filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="remote_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationParameters", propOrder = {
    "psKeyValue",
    "psKeyPath",
    "caPath",
    "certPath",
    "publicKeyPath",
    "publicKeyFilename",
    "publicKeyPassphrase",
    "caId",
    "caFilename",
    "certId",
    "certFilename",
    "remoteId"
})
public class AuthenticationParameters {

    @XmlElement(name = "psKey_value")
    protected String psKeyValue;
    @XmlElement(name = "psKey_path")
    protected String psKeyPath;
    @XmlElement(name = "ca_path")
    protected String caPath;
    @XmlElement(name = "cert_path")
    protected String certPath;
    @XmlElement(name = "publicKey_path")
    protected String publicKeyPath;
    @XmlElement(name = "publicKey_filename")
    protected String publicKeyFilename;
    @XmlElement(name = "publicKey_passphrase")
    protected String publicKeyPassphrase;
    @XmlElement(name = "ca_id")
    protected String caId;
    @XmlElement(name = "ca_filename")
    protected String caFilename;
    @XmlElement(name = "cert_id")
    protected String certId;
    @XmlElement(name = "cert_filename")
    protected String certFilename;
    @XmlElement(name = "remote_id")
    protected String remoteId;

    /**
     * Recupera il valore della propriet� psKeyValue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPsKeyValue() {
        return psKeyValue;
    }

    /**
     * Imposta il valore della propriet� psKeyValue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPsKeyValue(String value) {
        this.psKeyValue = value;
    }

    /**
     * Recupera il valore della propriet� psKeyPath.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPsKeyPath() {
        return psKeyPath;
    }

    /**
     * Imposta il valore della propriet� psKeyPath.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPsKeyPath(String value) {
        this.psKeyPath = value;
    }

    /**
     * Recupera il valore della propriet� caPath.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaPath() {
        return caPath;
    }

    /**
     * Imposta il valore della propriet� caPath.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaPath(String value) {
        this.caPath = value;
    }

    /**
     * Recupera il valore della propriet� certPath.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertPath() {
        return certPath;
    }

    /**
     * Imposta il valore della propriet� certPath.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertPath(String value) {
        this.certPath = value;
    }

    /**
     * Recupera il valore della propriet� publicKeyPath.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    /**
     * Imposta il valore della propriet� publicKeyPath.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicKeyPath(String value) {
        this.publicKeyPath = value;
    }

    /**
     * Recupera il valore della propriet� publicKeyFilename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicKeyFilename() {
        return publicKeyFilename;
    }

    /**
     * Imposta il valore della propriet� publicKeyFilename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicKeyFilename(String value) {
        this.publicKeyFilename = value;
    }

    /**
     * Recupera il valore della propriet� publicKeyPassphrase.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicKeyPassphrase() {
        return publicKeyPassphrase;
    }

    /**
     * Imposta il valore della propriet� publicKeyPassphrase.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicKeyPassphrase(String value) {
        this.publicKeyPassphrase = value;
    }

    /**
     * Recupera il valore della propriet� caId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaId() {
        return caId;
    }

    /**
     * Imposta il valore della propriet� caId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaId(String value) {
        this.caId = value;
    }

    /**
     * Recupera il valore della propriet� caFilename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaFilename() {
        return caFilename;
    }

    /**
     * Imposta il valore della propriet� caFilename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaFilename(String value) {
        this.caFilename = value;
    }

    /**
     * Recupera il valore della propriet� certId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertId() {
        return certId;
    }

    /**
     * Imposta il valore della propriet� certId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertId(String value) {
        this.certId = value;
    }

    /**
     * Recupera il valore della propriet� certFilename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertFilename() {
        return certFilename;
    }

    /**
     * Imposta il valore della propriet� certFilename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertFilename(String value) {
        this.certFilename = value;
    }

    /**
     * Recupera il valore della propriet� remoteId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteId() {
        return remoteId;
    }

    /**
     * Imposta il valore della propriet� remoteId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteId(String value) {
        this.remoteId = value;
    }

}