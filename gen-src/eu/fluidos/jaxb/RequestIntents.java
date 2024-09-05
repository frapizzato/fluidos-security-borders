//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.09.05 alle 05:37:39 PM CEST 
//


package eu.fluidos.jaxb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RequestIntents complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="RequestIntents"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}Configuration"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="acceptMonitoring" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="configurationRule" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationRule" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestIntents", propOrder = {
    "acceptMonitoring",
    "configurationRule"
})
public class RequestIntents
    extends Configuration
{

    protected boolean acceptMonitoring;
    protected List<ConfigurationRule> configurationRule;

    /**
     * Recupera il valore della proprietà acceptMonitoring.
     * 
     */
    public boolean isAcceptMonitoring() {
        return acceptMonitoring;
    }

    /**
     * Imposta il valore della proprietà acceptMonitoring.
     * 
     */
    public void setAcceptMonitoring(boolean value) {
        this.acceptMonitoring = value;
    }

    /**
     * Gets the value of the configurationRule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the configurationRule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfigurationRule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConfigurationRule }
     * 
     * 
     */
    public List<ConfigurationRule> getConfigurationRule() {
        if (configurationRule == null) {
            configurationRule = new ArrayList<ConfigurationRule>();
        }
        return this.configurationRule;
    }

}