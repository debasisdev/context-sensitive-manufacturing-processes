//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.11 at 12:05:10 AM CET 
//


package unistuttgart.iaas.spi.cmprocess.cmp;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for tProcessDefinition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tProcessDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tBaseType">
 *       &lt;sequence>
 *         &lt;element name="ProcessContent" type="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tContent"/>
 *         &lt;element name="TargetIntention" type="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tIntention"/>
 *         &lt;element name="InitialContexts" type="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tContextExpressions"/>
 *         &lt;element name="DesiredFinalContexts" type="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tContexts" minOccurs="0"/>
 *         &lt;element name="AlternativeFinalContexts" type="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tContexts" minOccurs="0"/>
 *         &lt;element name="Weight" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/sequence>
 *       &lt;attribute name="processType" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tProcessDefinition", propOrder = {
    "processContent",
    "targetIntention",
    "initialContexts",
    "desiredFinalContexts",
    "alternativeFinalContexts",
    "weight"
})
public class TProcessDefinition
    extends TBaseType
{

    @XmlElement(name = "ProcessContent", required = true)
    protected TContent processContent;
    @XmlElement(name = "TargetIntention", required = true)
    protected TIntention targetIntention;
    @XmlElement(name = "InitialContexts", required = true)
    protected TContextExpressions initialContexts;
    @XmlElement(name = "DesiredFinalContexts")
    protected TContexts desiredFinalContexts;
    @XmlElement(name = "AlternativeFinalContexts")
    protected TContexts alternativeFinalContexts;
    @XmlElement(name = "Weight", required = true)
    protected BigDecimal weight;
    @XmlAttribute(name = "processType")
    @XmlSchemaType(name = "anyURI")
    protected String processType;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the processContent property.
     * 
     * @return
     *     possible object is
     *     {@link TContent }
     *     
     */
    public TContent getProcessContent() {
        return processContent;
    }

    /**
     * Sets the value of the processContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link TContent }
     *     
     */
    public void setProcessContent(TContent value) {
        this.processContent = value;
    }

    /**
     * Gets the value of the targetIntention property.
     * 
     * @return
     *     possible object is
     *     {@link TIntention }
     *     
     */
    public TIntention getTargetIntention() {
        return targetIntention;
    }

    /**
     * Sets the value of the targetIntention property.
     * 
     * @param value
     *     allowed object is
     *     {@link TIntention }
     *     
     */
    public void setTargetIntention(TIntention value) {
        this.targetIntention = value;
    }

    /**
     * Gets the value of the initialContexts property.
     * 
     * @return
     *     possible object is
     *     {@link TContextExpressions }
     *     
     */
    public TContextExpressions getInitialContexts() {
        return initialContexts;
    }

    /**
     * Sets the value of the initialContexts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TContextExpressions }
     *     
     */
    public void setInitialContexts(TContextExpressions value) {
        this.initialContexts = value;
    }

    /**
     * Gets the value of the desiredFinalContexts property.
     * 
     * @return
     *     possible object is
     *     {@link TContexts }
     *     
     */
    public TContexts getDesiredFinalContexts() {
        return desiredFinalContexts;
    }

    /**
     * Sets the value of the desiredFinalContexts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TContexts }
     *     
     */
    public void setDesiredFinalContexts(TContexts value) {
        this.desiredFinalContexts = value;
    }

    /**
     * Gets the value of the alternativeFinalContexts property.
     * 
     * @return
     *     possible object is
     *     {@link TContexts }
     *     
     */
    public TContexts getAlternativeFinalContexts() {
        return alternativeFinalContexts;
    }

    /**
     * Sets the value of the alternativeFinalContexts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TContexts }
     *     
     */
    public void setAlternativeFinalContexts(TContexts value) {
        this.alternativeFinalContexts = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWeight(BigDecimal value) {
        this.weight = value;
    }

    /**
     * Gets the value of the processType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessType() {
        return processType;
    }

    /**
     * Sets the value of the processType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessType(String value) {
        this.processType = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
