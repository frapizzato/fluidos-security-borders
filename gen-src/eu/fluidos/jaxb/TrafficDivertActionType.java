//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.03.14 at 09:34:48 AM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrafficDivertActionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
