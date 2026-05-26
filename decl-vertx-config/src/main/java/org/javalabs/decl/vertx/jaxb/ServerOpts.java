package org.javalabs.decl.vertx.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="ssl" type="{http://www.w3.org/2001/XMLSchema}Boolean"/&gt;
 *         &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/&gt;
 *         &lt;element name="sys-port" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="accept-backlog" type="{http://www.w3.org/2001/XMLSchema}Integer"/&gt;
 *         &lt;element name="client-auth" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sni" type="{http://www.w3.org/2001/XMLSchema}Boolean"/&gt;
 *         &lt;element name="use-proxy-protocol" type="{http://www.w3.org/2001/XMLSchema}Boolean"/&gt;
 *         &lt;element name="proxy-protocol-timeout" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/&gt;
 *         &lt;element name="register-write-handler" type="{http://www.w3.org/2001/XMLSchema}Boolean"/&gt;
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
    "ssl",
    "host",
    "port",
    "sysPort",
    "acceptBacklog",
    "clientAuth",
    "sni",
    "useProxyProtocol",
    "proxyProtocolTimeout",
    "registerWriteHandler"
})
public class ServerOpts {

    @XmlElement(defaultValue = "false")
    protected Boolean ssl;
    
    @XmlElement(required = true, defaultValue = "0.0.0.0")
    protected String host;
    
    @XmlElement(required = true, defaultValue = "8080")
    protected Integer port;
    
    @XmlElement(required = true)
    protected String sysPort;
    
    @XmlElement(name = "accept-backlog", defaultValue = "-1")
    protected Integer acceptBacklog;
    
    @XmlElement(name = "client-auth", required = true, defaultValue = "NONE")
    protected String clientAuth;
    
    @XmlElement(defaultValue = "false")
    protected Boolean sni;
    
    @XmlElement(name = "use-proxy-protocol", defaultValue = "false")
    protected Boolean useProxyProtocol;
    
    @XmlElement(name = "proxy-protocol-timeout", defaultValue = "10000")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer proxyProtocolTimeout;
    
    @XmlElement(name = "register-write-handler", defaultValue = "false")
    protected Boolean registerWriteHandler;

    /**
     * Gets the value of the ssl property.
     *
     */
    public Boolean isSsl() {
        return ssl;
    }

    /**
     * Sets the value of the ssl property.
     *
     */
    public void setSsl(Boolean value) {
        this.ssl = value;
    }

    /**
     * Gets the value of the host property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the value of the host property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setHost(String value) {
        this.host = value;
    }

    /**
     * Gets the value of the port property.
     *
     * @return possible object is {@link String }
     *
     */
    public Integer getPort() {
        if (sysPort != null && sysPort.trim().length() > 0) {
            String portVal = _sys(sysPort);
            return Integer.valueOf(portVal);
        }
        return port;
    }

    /**
     * Sets the value of the port property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setPort(Integer value) {
        this.port = value;
    }

    /**
     * Gets the value of the port system property.
     *
     * @return possible object is {@link String }
     *
     */
    public Integer getSysPort() {
        return port;
    }

    /**
     * Sets the value of the port system property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setSysPort(Integer value) {
        this.port = value;
    }

    /**
     * Gets the value of the acceptBacklog property.
     *
     */
    public Integer getAcceptBacklog() {
        return acceptBacklog;
    }

    /**
     * Sets the value of the acceptBacklog property.
     *
     */
    public void setAcceptBacklog(Integer value) {
        this.acceptBacklog = value;
    }

    /**
     * Gets the value of the clientAuth property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getClientAuth() {
        return clientAuth;
    }

    /**
     * Sets the value of the clientAuth property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setClientAuth(String value) {
        this.clientAuth = value;
    }

    /**
     * Gets the value of the sni property.
     *
     */
    public Boolean isSni() {
        return sni;
    }

    /**
     * Sets the value of the sni property.
     *
     */
    public void setSni(Boolean value) {
        this.sni = value;
    }

    /**
     * Gets the value of the useProxyProtocol property.
     *
     */
    public Boolean isUseProxyProtocol() {
        return useProxyProtocol;
    }

    /**
     * Sets the value of the useProxyProtocol property.
     *
     */
    public void setUseProxyProtocol(Boolean value) {
        this.useProxyProtocol = value;
    }

    /**
     * Gets the value of the proxyProtocolTimeout property.
     *
     */
    public Integer getProxyProtocolTimeout() {
        return proxyProtocolTimeout;
    }

    /**
     * Sets the value of the proxyProtocolTimeout property.
     *
     */
    public void setProxyProtocolTimeout(Integer value) {
        this.proxyProtocolTimeout = value;
    }

    /**
     * Gets the value of the registerWriteHandler property.
     *
     * @return 
     */
    public Boolean isRegisterWriteHandler() {
        return registerWriteHandler;
    }

    /**
     * Sets the value of the registerWriteHandler property.
     *
     * @param value
     */
    public void setRegisterWriteHandler(Boolean value) {
        this.registerWriteHandler = value;
    }
    
    public String _sys(String value) {
        int start = value.indexOf("system(");
        if (start == -1) {
            start = value.indexOf("SYSTEM(");
        }
        if (start == 0) {
            int end = value.indexOf(")");
            if (end > 0) {
                String param = value.substring(7, end);
                String[] tmp = param.split("::");
                if (tmp.length == 2) {
                    String val = System.getProperty(tmp[0], tmp[1]);
                    return val;
                }
                else {
                    String val = System.getProperty(tmp[0]);
                    return val;
                }
            }
        }
        return value;
    }

}
