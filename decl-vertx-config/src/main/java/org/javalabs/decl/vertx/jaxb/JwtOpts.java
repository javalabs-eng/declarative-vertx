package org.javalabs.decl.vertx.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 * @author Sudiptasish Chanda
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "algorithm",
    "issuer",
    "audience",
    "expiry"
})
public class JwtOpts {

    @XmlElement(required = true)
    protected String algorithm = "RS256";
    
    @XmlElement(required = true)
    protected String issuer;
    
    @XmlElement(required = true)
    protected String audience;
    
    @XmlElement(required = true)
    protected int expiry = 60;      // 60 minutes

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String value) {
        this.issuer = value;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String value) {
        this.audience = value;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int value) {
        this.expiry = value;
    }
}
