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
 * <p>Classe Java per PrivacyPKIMethod complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PrivacyPKIMethod"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PrivacyMethod"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="pkiParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AuthenticationParameters"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrivacyPKIMethod", propOrder = {
    "pkiParameters"
})
public class PrivacyPKIMethod
    extends PrivacyMethod
{

    @XmlElement(required = true)
    protected AuthenticationParameters pkiParameters;

    /**
     * Recupera il valore della propriet� pkiParameters.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationParameters }
     *     
     */
    public AuthenticationParameters getPkiParameters() {
        return pkiParameters;
    }

    /**
     * Imposta il valore della propriet� pkiParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationParameters }
     *     
     */
    public void setPkiParameters(AuthenticationParameters value) {
        this.pkiParameters = value;
    }

}