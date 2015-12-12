//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.11 at 12:05:10 AM CET 
//


package unistuttgart.iaas.spi.cmprocess.cmp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tIntention complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tIntention">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tBaseType">
 *       &lt;sequence>
 *         &lt;element name="Definition" type="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tDefinition" maxOccurs="unbounded"/>
 *         &lt;element name="SubIntentions" type="{http://www.uni-stuttgart.de/iaas/cmp/v0.1/}tIntentions" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tIntention", propOrder = {
    "definition",
    "subIntentions"
})
@XmlRootElement
public class TIntention
    extends TBaseType
{

    @XmlElement(name = "Definition", required = true)
    protected List<TDefinition> definition;
    @XmlElement(name = "SubIntentions")
    protected TIntentions subIntentions;

    /**
     * Gets the value of the definition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the definition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDefinition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TDefinition }
     * 
     * 
     */
    public List<TDefinition> getDefinition() {
        if (definition == null) {
            definition = new ArrayList<TDefinition>();
        }
        return this.definition;
    }

    /**
     * Gets the value of the subIntentions property.
     * 
     * @return
     *     possible object is
     *     {@link TIntentions }
     *     
     */
    public TIntentions getSubIntentions() {
        return subIntentions;
    }

    /**
     * Sets the value of the subIntentions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TIntentions }
     *     
     */
    public void setSubIntentions(TIntentions value) {
        this.subIntentions = value;
    }

}