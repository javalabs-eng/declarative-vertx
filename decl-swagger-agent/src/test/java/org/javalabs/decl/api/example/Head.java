package org.javalabs.decl.api.example;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Head {
    
    public static enum AUTHORITY {ADMIN, VP, ED};
    
    private String name;
    private String title;
    private AUTHORITY auth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AUTHORITY getAuth() {
        return auth;
    }

    public void setAuth(AUTHORITY auth) {
        this.auth = auth;
    }
    
}
