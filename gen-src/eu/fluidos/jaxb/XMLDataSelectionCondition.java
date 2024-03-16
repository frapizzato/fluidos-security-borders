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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XMLDataSelectionCondition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XMLDataSelectionCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}DataSelectionCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="xmlDataType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlNameSpace" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlQueryLanguage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlQueryLanguageVersion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlQueryExpression" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XMLDataSelectionCondition", propOrder = {
    "xmlDataType",
    "xmlNameSpace",
    "xmlQueryLanguage",
    "xmlQueryLanguageVersion",
    "xmlQueryExpression"
})
@XmlSeeAlso({
    WSSecurityCondition.class
})
public class XMLDataSelectionCondition
    extends DataSelectionCondition
{

    @XmlElement(required = true)
    protected String xmlDataType;
    @XmlElement(required = true)
    protected String xmlNameSpace;
    @XmlElement(required = true)
    protected String xmlQueryLanguage;
    @XmlElement(required = true)
    protected String xmlQueryLanguageVersion;
    @XmlElement(required = true)
    protected String xmlQueryExpression;

    /**
     * Gets the value of the xmlDataType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlDataType() {
        return xmlDataType;
    }

    /**
     * Sets the value of the xmlDataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlDataType(String value) {
        this.xmlDataType = value;
    }

    /**
     * Gets the value of the xmlNameSpace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlNameSpace() {
        return xmlNameSpace;
    }

    /**
     * Sets the value of the xmlNameSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlNameSpace(String value) {
        this.xmlNameSpace = value;
    }

    /**
     * Gets the value of the xmlQueryLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlQueryLanguage() {
        return xmlQueryLanguage;
    }

    /**
     * Sets the value of the xmlQueryLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlQueryLanguage(String value) {
        this.xmlQueryLanguage = value;
    }

    /**
     * Gets the value of the xmlQueryLanguageVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlQueryLanguageVersion() {
        return xmlQueryLanguageVersion;
    }

    /**
     * Sets the value of the xmlQueryLanguageVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlQueryLanguageVersion(String value) {
        this.xmlQueryLanguageVersion = value;
    }

    /**
     * Gets the value of the xmlQueryExpression property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlQueryExpression() {
        return xmlQueryExpression;
    }

    /**
     * Sets the value of the xmlQueryExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlQueryExpression(String value) {
        this.xmlQueryExpression = value;
    }

}
