//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.15 at 03:46:13 AM CET 
//


package demo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tCapabilityProperties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tCapabilityProperties">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProvidingResources" type="{http://www.uni-stuttgart.de/iaas/ipsm/v0.2/}tResourceReferences"/>
 *         &lt;element name="DesiredResource" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCapabilityProperties", namespace = "http://www.uni-stuttgart.de/iaas/ipsm/v0.2/", propOrder = {
    "providingResources",
    "desiredResource"
})
public class TCapabilityProperties {

    @XmlElement(name = "ProvidingResources", required = true)
    protected TResourceReferences providingResources;
    @XmlElement(name = "DesiredResource", required = true)
    protected QName desiredResource;

    /**
     * Gets the value of the providingResources property.
     * 
     * @return
     *     possible object is
     *     {@link TResourceReferences }
     *     
     */
    public TResourceReferences getProvidingResources() {
        return providingResources;
    }

    /**
     * Sets the value of the providingResources property.
     * 
     * @param value
     *     allowed object is
     *     {@link TResourceReferences }
     *     
     */
    public void setProvidingResources(TResourceReferences value) {
        this.providingResources = value;
    }

    /**
     * Gets the value of the desiredResource property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getDesiredResource() {
        return desiredResource;
    }

    /**
     * Sets the value of the desiredResource property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setDesiredResource(QName value) {
        this.desiredResource = value;
    }

}
