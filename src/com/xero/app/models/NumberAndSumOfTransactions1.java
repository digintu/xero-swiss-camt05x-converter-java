//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.08 at 10:37:47 AM CEST 
//


package com.xero.app.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;


/**
 * <p>Java class for NumberAndSumOfTransactions1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NumberAndSumOfTransactions1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NbOfNtries" type="{urn:iso:std:iso:20022:tech:xsd:camt.053.001.02}Max15NumericText" minOccurs="0"/>
 *         &lt;element name="Sum" type="{urn:iso:std:iso:20022:tech:xsd:camt.053.001.02}DecimalNumber" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NumberAndSumOfTransactions1", propOrder = {
    "nbOfNtries",
    "sum"
})
public class NumberAndSumOfTransactions1 {

    @XmlElement(name = "NbOfNtries")
    protected String nbOfNtries;
    @XmlElement(name = "Sum")
    protected BigDecimal sum;

    /**
     * Gets the value of the nbOfNtries property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNbOfNtries() {
        return nbOfNtries;
    }

    /**
     * Sets the value of the nbOfNtries property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNbOfNtries(String value) {
        this.nbOfNtries = value;
    }

    /**
     * Gets the value of the sum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getSum() {
        return sum;
    }

    /**
     * Sets the value of the sum property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSum(BigDecimal value) {
        this.sum = value;
    }

}
