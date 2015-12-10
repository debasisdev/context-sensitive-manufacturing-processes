//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.09 at 01:43:47 AM CET 
//


package unistuttgart.iaas.spi.cmprocess.processgen;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tProcessDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tProcessDefinitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Documentation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProcessContent" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Intention" type="{}IntentionType"/>
 *         &lt;element name="InitialContext" type="{}InitialContextType"/>
 *         &lt;element name="Weight" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetNamespace" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tProcessDefinitionType", propOrder = {
    "documentation",
    "processContent",
    "intention",
    "initialContext",
    "weight"
})
public class TProcessDefinitionType {

    @XmlElement(name = "Documentation", required = true)
    protected String documentation;
    @XmlElement(name = "ProcessContent", required = true)
    protected String processContent;
    @XmlElement(name = "Intention", required = true)
    protected IntentionType intention;
    @XmlElement(name = "InitialContext", required = true)
    protected InitialContextType initialContext;
    @XmlElement(name = "Weight", required = true)
    protected BigDecimal weight;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "targetNamespace")
    protected String targetNamespace;

    /**
     * Gets the value of the documentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the processContent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessContent() {
        return processContent;
    }

    /**
     * Sets the value of the processContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessContent(String value) {
        this.processContent = value;
    }

    /**
     * Gets the value of the intention property.
     * 
     * @return
     *     possible object is
     *     {@link IntentionType }
     *     
     */
    public IntentionType getIntention() {
        return intention;
    }

    /**
     * Sets the value of the intention property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntentionType }
     *     
     */
    public void setIntention(IntentionType value) {
        this.intention = value;
    }

    /**
     * Gets the value of the initialContext property.
     * 
     * @return
     *     possible object is
     *     {@link InitialContextType }
     *     
     */
    public InitialContextType getInitialContext() {
        return initialContext;
    }

    /**
     * Sets the value of the initialContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link InitialContextType }
     *     
     */
    public void setInitialContext(InitialContextType value) {
        this.initialContext = value;
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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the targetNamespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

}
