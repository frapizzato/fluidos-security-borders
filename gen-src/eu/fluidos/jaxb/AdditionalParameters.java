//
// Questo file � stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per AdditionalParameters complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AdditionalParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="encoderLayers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="decoderLayers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="encoderNeurons" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="decoderNeurons" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codeNeurons" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lossFunction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="epsilon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="delta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdditionalParameters", propOrder = {
    "encoderLayers",
    "decoderLayers",
    "encoderNeurons",
    "decoderNeurons",
    "codeNeurons",
    "lossFunction",
    "epsilon",
    "delta"
})
public class AdditionalParameters
    extends ConfigurationCondition
{

    protected String encoderLayers;
    protected String decoderLayers;
    protected String encoderNeurons;
    protected String decoderNeurons;
    protected String codeNeurons;
    protected String lossFunction;
    protected String epsilon;
    protected String delta;

    /**
     * Recupera il valore della propriet� encoderLayers.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncoderLayers() {
        return encoderLayers;
    }

    /**
     * Imposta il valore della propriet� encoderLayers.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncoderLayers(String value) {
        this.encoderLayers = value;
    }

    /**
     * Recupera il valore della propriet� decoderLayers.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecoderLayers() {
        return decoderLayers;
    }

    /**
     * Imposta il valore della propriet� decoderLayers.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecoderLayers(String value) {
        this.decoderLayers = value;
    }

    /**
     * Recupera il valore della propriet� encoderNeurons.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncoderNeurons() {
        return encoderNeurons;
    }

    /**
     * Imposta il valore della propriet� encoderNeurons.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncoderNeurons(String value) {
        this.encoderNeurons = value;
    }

    /**
     * Recupera il valore della propriet� decoderNeurons.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecoderNeurons() {
        return decoderNeurons;
    }

    /**
     * Imposta il valore della propriet� decoderNeurons.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecoderNeurons(String value) {
        this.decoderNeurons = value;
    }

    /**
     * Recupera il valore della propriet� codeNeurons.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeNeurons() {
        return codeNeurons;
    }

    /**
     * Imposta il valore della propriet� codeNeurons.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeNeurons(String value) {
        this.codeNeurons = value;
    }

    /**
     * Recupera il valore della propriet� lossFunction.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLossFunction() {
        return lossFunction;
    }

    /**
     * Imposta il valore della propriet� lossFunction.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLossFunction(String value) {
        this.lossFunction = value;
    }

    /**
     * Recupera il valore della propriet� epsilon.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpsilon() {
        return epsilon;
    }

    /**
     * Imposta il valore della propriet� epsilon.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpsilon(String value) {
        this.epsilon = value;
    }

    /**
     * Recupera il valore della propriet� delta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelta() {
        return delta;
    }

    /**
     * Imposta il valore della propriet� delta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelta(String value) {
        this.delta = value;
    }

}
