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
 * <p>Java class for MeasureUnit.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="MeasureUnit"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MESSAGE"/&gt;
 *     &lt;enumeration value="DATAGRAM"/&gt;
 *     &lt;enumeration value="CONNECTION"/&gt;
 *     &lt;enumeration value="CONNECTION_DURATION"/&gt;
 *     &lt;enumeration value="BYTE"/&gt;
 *     &lt;enumeration value="KBYTE"/&gt;
 *     &lt;enumeration value="MBYTE"/&gt;
 *     &lt;enumeration value="GBYTE"/&gt;
 *     &lt;enumeration value="TBYTE"/&gt;
 *     &lt;enumeration value="PBYTE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "MeasureUnit")
@XmlEnum
public enum MeasureUnit {

    MESSAGE,
    DATAGRAM,
    CONNECTION,
    CONNECTION_DURATION,
    BYTE,
    KBYTE,
    MBYTE,
    GBYTE,
    TBYTE,
    PBYTE;

    public String value() {
        return name();
    }

    public static MeasureUnit fromValue(String v) {
        return valueOf(v);
    }

}
