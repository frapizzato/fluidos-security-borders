//
// Questo file è stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.03.28 alle 11:20:02 AM GMT+01:00 
//


package eu.fluidos.jaxb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Counts complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Counts"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="isCNF" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="count" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}Count" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Counts", propOrder = {
    "isCNF",
    "count"
})
public class Counts {

    protected boolean isCNF;
    @XmlElement(required = true)
    protected List<Count> count;

    /**
     * Recupera il valore della proprietà isCNF.
     * 
     */
    public boolean isIsCNF() {
        return isCNF;
    }

    /**
     * Imposta il valore della proprietà isCNF.
     * 
     */
    public void setIsCNF(boolean value) {
        this.isCNF = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the count property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Count }
     * 
     * 
     */
    public List<Count> getCount() {
        if (count == null) {
            count = new ArrayList<Count>();
        }
        return this.count;
    }

}
