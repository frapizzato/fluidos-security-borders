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
 * <p>Classe Java per KubernetesNetworkFilteringAction complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="KubernetesNetworkFilteringAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="KubernetesNetworkFilteringActionType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KubernetesNetworkFilteringAction", propOrder = {
    "kubernetesNetworkFilteringActionType"
})
public class KubernetesNetworkFilteringAction
    extends ConfigurationAction
{

    @XmlElement(name = "KubernetesNetworkFilteringActionType", required = true)
    protected String kubernetesNetworkFilteringActionType;

    /**
     * Recupera il valore della propriet� kubernetesNetworkFilteringActionType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKubernetesNetworkFilteringActionType() {
        return kubernetesNetworkFilteringActionType;
    }

    /**
     * Imposta il valore della propriet� kubernetesNetworkFilteringActionType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKubernetesNetworkFilteringActionType(String value) {
        this.kubernetesNetworkFilteringActionType = value;
    }

}