//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.03.28 alle 11:20:02 AM GMT+01:00 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per FederationLearnigActionType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <pre>
 * &lt;simpleType name="FederationLearnigActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AGGREGATOR"/&gt;
 *     &lt;enumeration value="DETECTION"/&gt;
 *     &lt;enumeration value="AGENT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FederationLearnigActionType")
@XmlEnum
public enum FederationLearnigActionType {

    AGGREGATOR,
    DETECTION,
    AGENT;

    public String value() {
        return name();
    }

    public static FederationLearnigActionType fromValue(String v) {
        return valueOf(v);
    }

}
