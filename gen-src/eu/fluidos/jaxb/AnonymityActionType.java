//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.09.10 alle 04:35:03 PM CEST 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per AnonymityActionType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <pre>
 * &lt;simpleType name="AnonymityActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SENDER"/&gt;
 *     &lt;enumeration value="RECEIVER"/&gt;
 *     &lt;enumeration value="COMMUNICATION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AnonymityActionType")
@XmlEnum
public enum AnonymityActionType {

    SENDER,
    RECEIVER,
    COMMUNICATION;

    public String value() {
        return name();
    }

    public static AnonymityActionType fromValue(String v) {
        return valueOf(v);
    }

}
