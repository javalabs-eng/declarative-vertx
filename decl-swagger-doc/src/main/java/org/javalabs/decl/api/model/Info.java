package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({ "version", "title", "description", "termsOfService", "contact", "license" })
public class Info {
 
    private String version;
    private String title;
    private String description;
    private String termsOfService;
    private Contact contact;
    private License license;
    
    public Info() {}

    public Info(String version, String title, String description, String termsOfService) {
        this.version = version;
        this.title = title;
        this.description = description;
        this.termsOfService = termsOfService;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }
}
