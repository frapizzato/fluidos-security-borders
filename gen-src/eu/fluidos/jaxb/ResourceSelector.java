//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.13 alle 07:01:47 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ResourceSelector complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ResourceSelector"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="isHostCluster" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceSelector", propOrder = {
    "isHostCluster"
})
@XmlSeeAlso({
    PodNamespaceSelector.class,
    CIDRSelector.class
})
public class ResourceSelector {

    protected boolean isHostCluster;

    /**
     * Recupera il valore della proprietà isHostCluster.
     * 
     */
    public boolean isIsHostCluster() {
        return isHostCluster;
    }

    /**
     * Imposta il valore della proprietà isHostCluster.
     * 
     */
    public void setIsHostCluster(boolean value) {
        this.isHostCluster = value;
    }

}
