//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.03.14 at 09:34:48 AM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParentalControlAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParentalControlAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}EnableAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="pics" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}Pics" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParentalControlAction", propOrder = {
    "pics"
})
public class ParentalControlAction
    extends EnableAction
{

    protected Pics pics;

    /**
     * Gets the value of the pics property.
     * 
     * @return
     *     possible object is
     *     {@link Pics }
     *     
     */
    public Pics getPics() {
        return pics;
    }

    /**
     * Sets the value of the pics property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pics }
     *     
     */
    public void setPics(Pics value) {
        this.pics = value;
    }

}
