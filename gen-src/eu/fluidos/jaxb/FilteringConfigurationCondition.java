//
// Questo file � stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2024.02.18 alle 05:37:56 PM CET 
//


package eu.fluidos.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per FilteringConfigurationCondition complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="FilteringConfigurationCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="packetFilterCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PacketFilterCondition" minOccurs="0"/&gt;
 *         &lt;element name="ICMPCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ICMPCondition" minOccurs="0"/&gt;
 *         &lt;element name="statefulCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}StatefulCondition" minOccurs="0"/&gt;
 *         &lt;element name="timeCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TimeCondition" minOccurs="0"/&gt;
 *         &lt;element name="applicationLayerCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ApplicationLayerCondition" minOccurs="0"/&gt;
 *         &lt;element name="aggregatorParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AggregatorParameters" minOccurs="0"/&gt;
 *         &lt;element name="localConnectionParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}LocalConnectionParameters" minOccurs="0"/&gt;
 *         &lt;element name="qosCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}QoSCondition" minOccurs="0"/&gt;
 *         &lt;element name="networkSlicingConditionParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}NetworkSlicingConditionParameters" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilteringConfigurationCondition", propOrder = {
    "packetFilterCondition",
    "icmpCondition",
    "statefulCondition",
    "timeCondition",
    "applicationLayerCondition",
    "aggregatorParameters",
    "localConnectionParameters",
    "qosCondition",
    "networkSlicingConditionParameters"
})
@XmlSeeAlso({
    PrivacyConfigurationCondition.class,
    DataAggregationConfigurationCondition.class,
    AnonymityConfigurationCondition.class,
    MonitoringConfigurationCondition.class,
    TrafficDivertConfigurationCondition.class,
    AuthorizationCondition.class,
    AuthenticationCondition.class
})
public class FilteringConfigurationCondition
    extends ConfigurationCondition
{

    protected PacketFilterCondition packetFilterCondition;
    @XmlElement(name = "ICMPCondition")
    protected ICMPCondition icmpCondition;
    protected StatefulCondition statefulCondition;
    protected TimeCondition timeCondition;
    protected ApplicationLayerCondition applicationLayerCondition;
    protected AggregatorParameters aggregatorParameters;
    protected LocalConnectionParameters localConnectionParameters;
    protected QoSCondition qosCondition;
    protected NetworkSlicingConditionParameters networkSlicingConditionParameters;

    /**
     * Recupera il valore della propriet� packetFilterCondition.
     * 
     * @return
     *     possible object is
     *     {@link PacketFilterCondition }
     *     
     */
    public PacketFilterCondition getPacketFilterCondition() {
        return packetFilterCondition;
    }

    /**
     * Imposta il valore della propriet� packetFilterCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketFilterCondition }
     *     
     */
    public void setPacketFilterCondition(PacketFilterCondition value) {
        this.packetFilterCondition = value;
    }

    /**
     * Recupera il valore della propriet� icmpCondition.
     * 
     * @return
     *     possible object is
     *     {@link ICMPCondition }
     *     
     */
    public ICMPCondition getICMPCondition() {
        return icmpCondition;
    }

    /**
     * Imposta il valore della propriet� icmpCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link ICMPCondition }
     *     
     */
    public void setICMPCondition(ICMPCondition value) {
        this.icmpCondition = value;
    }

    /**
     * Recupera il valore della propriet� statefulCondition.
     * 
     * @return
     *     possible object is
     *     {@link StatefulCondition }
     *     
     */
    public StatefulCondition getStatefulCondition() {
        return statefulCondition;
    }

    /**
     * Imposta il valore della propriet� statefulCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link StatefulCondition }
     *     
     */
    public void setStatefulCondition(StatefulCondition value) {
        this.statefulCondition = value;
    }

    /**
     * Recupera il valore della propriet� timeCondition.
     * 
     * @return
     *     possible object is
     *     {@link TimeCondition }
     *     
     */
    public TimeCondition getTimeCondition() {
        return timeCondition;
    }

    /**
     * Imposta il valore della propriet� timeCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeCondition }
     *     
     */
    public void setTimeCondition(TimeCondition value) {
        this.timeCondition = value;
    }

    /**
     * Recupera il valore della propriet� applicationLayerCondition.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationLayerCondition }
     *     
     */
    public ApplicationLayerCondition getApplicationLayerCondition() {
        return applicationLayerCondition;
    }

    /**
     * Imposta il valore della propriet� applicationLayerCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationLayerCondition }
     *     
     */
    public void setApplicationLayerCondition(ApplicationLayerCondition value) {
        this.applicationLayerCondition = value;
    }

    /**
     * Recupera il valore della propriet� aggregatorParameters.
     * 
     * @return
     *     possible object is
     *     {@link AggregatorParameters }
     *     
     */
    public AggregatorParameters getAggregatorParameters() {
        return aggregatorParameters;
    }

    /**
     * Imposta il valore della propriet� aggregatorParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link AggregatorParameters }
     *     
     */
    public void setAggregatorParameters(AggregatorParameters value) {
        this.aggregatorParameters = value;
    }

    /**
     * Recupera il valore della propriet� localConnectionParameters.
     * 
     * @return
     *     possible object is
     *     {@link LocalConnectionParameters }
     *     
     */
    public LocalConnectionParameters getLocalConnectionParameters() {
        return localConnectionParameters;
    }

    /**
     * Imposta il valore della propriet� localConnectionParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalConnectionParameters }
     *     
     */
    public void setLocalConnectionParameters(LocalConnectionParameters value) {
        this.localConnectionParameters = value;
    }

    /**
     * Recupera il valore della propriet� qosCondition.
     * 
     * @return
     *     possible object is
     *     {@link QoSCondition }
     *     
     */
    public QoSCondition getQosCondition() {
        return qosCondition;
    }

    /**
     * Imposta il valore della propriet� qosCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link QoSCondition }
     *     
     */
    public void setQosCondition(QoSCondition value) {
        this.qosCondition = value;
    }

    /**
     * Recupera il valore della propriet� networkSlicingConditionParameters.
     * 
     * @return
     *     possible object is
     *     {@link NetworkSlicingConditionParameters }
     *     
     */
    public NetworkSlicingConditionParameters getNetworkSlicingConditionParameters() {
        return networkSlicingConditionParameters;
    }

    /**
     * Imposta il valore della propriet� networkSlicingConditionParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkSlicingConditionParameters }
     *     
     */
    public void setNetworkSlicingConditionParameters(NetworkSlicingConditionParameters value) {
        this.networkSlicingConditionParameters = value;
    }

}
