//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.01 at 03:05:51 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for interactionLevel.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="interactionLevel"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="LOW"/&gt;
 *     &lt;enumeration value="MEDIUM"/&gt;
 *     &lt;enumeration value="HIGH"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "interactionLevel", namespace = "http://www.example.org/IoTHoneynetSchema")
@XmlEnum
public enum InteractionLevel {

    LOW,
    MEDIUM,
    HIGH;

    public String value() {
        return name();
    }

    public static InteractionLevel fromValue(String v) {
        return valueOf(v);
    }

}
