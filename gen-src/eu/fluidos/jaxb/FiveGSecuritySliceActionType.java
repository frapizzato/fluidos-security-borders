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
 * <p>Java class for FiveGSecuritySliceActionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="FiveGSecuritySliceActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="DEPLOY"/&gt;
 *     &lt;enumeration value="UPDATE"/&gt;
 *     &lt;enumeration value="REMOVE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FiveGSecuritySliceActionType")
@XmlEnum
public enum FiveGSecuritySliceActionType {

    DEPLOY,
    UPDATE,
    REMOVE;

    public String value() {
        return name();
    }

    public static FiveGSecuritySliceActionType fromValue(String v) {
        return valueOf(v);
    }

}