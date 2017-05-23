
package com.expertedge.entrustplugin.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for adminResponseDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminResponseDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="adminSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="respCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="respMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="respMessageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminResponseDTO", propOrder = {
    "adminSuccessful",
    "respCode",
    "respMessage",
    "respMessageCode"
})
public class AdminResponseDTO {

    protected boolean adminSuccessful;
    protected Integer respCode;
    protected String respMessage;
    protected String respMessageCode;

    /**
     * Gets the value of the adminSuccessful property.
     * 
     */
    public boolean isAdminSuccessful() {
        return adminSuccessful;
    }

    /**
     * Sets the value of the adminSuccessful property.
     * 
     */
    public void setAdminSuccessful(boolean value) {
        this.adminSuccessful = value;
    }

    /**
     * Gets the value of the respCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRespCode() {
        return respCode;
    }

    /**
     * Sets the value of the respCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRespCode(Integer value) {
        this.respCode = value;
    }

    /**
     * Gets the value of the respMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRespMessage() {
        return respMessage;
    }

    /**
     * Sets the value of the respMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRespMessage(String value) {
        this.respMessage = value;
    }

    /**
     * Gets the value of the respMessageCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRespMessageCode() {
        return respMessageCode;
    }

    /**
     * Sets the value of the respMessageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRespMessageCode(String value) {
        this.respMessageCode = value;
    }

}
