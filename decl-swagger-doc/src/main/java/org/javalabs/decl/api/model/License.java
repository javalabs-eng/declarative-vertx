package org.javalabs.decl.api.model;

/**
 *
 * @author Sudiptasish Chanda
 */
public class License {
    
    private String name;
    private String url;
    
    public License() {}

    public License(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
