//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.08.11 alle 11:12:47 AM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ioTResourceType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <pre>
 * &lt;simpleType name="ioTResourceType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TEMPERATURE"/&gt;
 *     &lt;enumeration value="HUMIDITY"/&gt;
 *     &lt;enumeration value="PRESSURE"/&gt;
 *     &lt;enumeration value="LIGHT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ioTResourceType", namespace = "http://www.example.org/IoTHoneynetSchema")
@XmlEnum
public enum IoTResourceType {

    TEMPERATURE,
    HUMIDITY,
    PRESSURE,
    LIGHT;

    public String value() {
        return name();
    }

    public static IoTResourceType fromValue(String v) {
        return valueOf(v);
    }

}
