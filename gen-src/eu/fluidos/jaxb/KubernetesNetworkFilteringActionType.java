//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.08.21 alle 04:24:06 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per KubernetesNetworkFilteringActionType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <pre>
 * &lt;simpleType name="KubernetesNetworkFilteringActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ALLOW"/&gt;
 *     &lt;enumeration value="DENY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "KubernetesNetworkFilteringActionType")
@XmlEnum
public enum KubernetesNetworkFilteringActionType {

    ALLOW,
    DENY;

    public String value() {
        return name();
    }

    public static KubernetesNetworkFilteringActionType fromValue(String v) {
        return valueOf(v);
    }

}
