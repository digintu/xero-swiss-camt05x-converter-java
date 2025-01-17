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
import javax.xml.bind.annotation.XmlRootElement;


/**
 * <p>Java class for Document complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BkToCstmrStmt" type="BankToCustomerStatementV02" minOccurs="0"/>
 *         &lt;element name="BkToCstmrDbtCdtNtfctn" type="BankToCustomerNotification" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", propOrder = {
    "bkToCstmrStmt",
    "bkToCstmrDbtCdtNtfctn"
})
@XmlRootElement(name = "Document")
public class Document {

	@XmlElement(name = "BkToCstmrStmt")
	protected BankToCustomerStatementV02 bkToCstmrStmt;

    @XmlElement(name = "BkToCstmrDbtCdtNtfctn")
    protected BankToCustomerNotification bkToCstmrDbtCdtNtfctn;

    /**
     * Gets the value of the bkToCstmrStmt property.
     * 
     * @return
     *     possible object is
     *     {@link BankToCustomerStatementV02 }
     *     
     */
    public BankToCustomerStatementV02 getBkToCstmrStmt() {
        return bkToCstmrStmt;
    }

    /**
     * Sets the value of the bkToCstmrStmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankToCustomerStatementV02 }
     *     
     */
    public void setBkToCstmrStmt(BankToCustomerStatementV02 value) {
        this.bkToCstmrStmt = value;
    }

	public BankToCustomerNotification getBkToCstmrDbtCdtNtfctn() {
		return bkToCstmrDbtCdtNtfctn;
	}

	public void setBkToCstmrDbtCdtNtfctn(BankToCustomerNotification bkToCstmrDbtCdtNtfctn) {
		this.bkToCstmrDbtCdtNtfctn = bkToCstmrDbtCdtNtfctn;
	}
    
}
