//
// Questo file � stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per TrafficDivertActionType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <pre>
 * &lt;simpleType name="TrafficDivertActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="FORWARD"/&gt;
 *     &lt;enumeration value="MIRRORING"/&gt;
 *     &lt;enumeration value="NAT"/&gt;
 *     &lt;enumeration value="ENCAPSULATION"/&gt;
 *     &lt;enumeration value="CREATE"/&gt;
 *     &lt;enumeration value="ATTACH"/&gt;
 *     &lt;enumeration value="DELETE"/&gt;
 *     &lt;enumeration value="DEATTACH"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TrafficDivertActionType")
@XmlEnum
public enum TrafficDivertActionType {

    FORWARD,
    MIRRORING,
    NAT,
    ENCAPSULATION,
    CREATE,
    ATTACH,
    DELETE,
    DEATTACH;

    public String value() {
        return name();
    }

    public static TrafficDivertActionType fromValue(String v) {
        return valueOf(v);
    }

}