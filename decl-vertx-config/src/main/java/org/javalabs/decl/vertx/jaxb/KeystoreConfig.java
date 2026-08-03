package org.javalabs.decl.vertx.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;complexType&gt;
 *           &lt;complexContent&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *               &lt;sequence&gt;
 *                 &lt;element name="key-path" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *                 &lt;element name="cert-path" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *               &lt;/sequence&gt;
 *             &lt;/restriction&gt;
 *           &lt;/complexContent&gt;
 *         &lt;/complexType&gt;
 *         &lt;element name="store-name" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="store-password" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pemKeyConfig",
    "storeName",
    "storePassword"
})
public class KeystoreConfig {
    
    @XmlElement(name = "pem-key-config", required = true)
    protected PemKeyConfig pemKeyConfig;

    @XmlElement(name = "store-name", required = true)
    protected String storeName;
    
    @XmlElement(name = "store-password", required = true)
    protected String storePassword;

    /**
     * Gets the value of the pemKeyConfig property.
     *
     * @return possible object is {@link Object }
     *
     */
    public PemKeyConfig getPemKeyConfig() {
        return pemKeyConfig;
    }

    /**
     * Sets the value of the pemKeyConfig property.
     *
     * @param value allowed object is {@link Object }
     *
     */
    public void setPemKeyConfig(PemKeyConfig value) {
        this.pemKeyConfig = value;
    }

    /**
     * Gets the value of the storeName property.
     *
     * @return possible object is {@link Object }
     *
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * Sets the value of the storeName property.
     *
     * @param value allowed object is {@link Object }
     *
     */
    public void setStoreName(String value) {
        this.storeName = value;
    }

    /**
     * Gets the value of the storePassword property.
     *
     * @return possible object is {@link Object }
     *
     */
    public String getStorePassword() {
        return storePassword;
    }

    /**
     * Sets the value of the storePassword property.
     *
     * @param value allowed object is {@link Object }
     *
     */
    public void setStorePassword(String value) {
        this.storePassword = value;
    }

}
