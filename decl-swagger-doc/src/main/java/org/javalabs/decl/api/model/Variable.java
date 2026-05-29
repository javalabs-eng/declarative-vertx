package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Variable {
    
    @JsonProperty("default")
    private String _default;
    private String description;
    
    @JsonProperty("enum")
    private List<String> _enum;
    
    public Variable() {}

    public Variable(String description, String _default, List<String> _enum) {
        this.description = description;
        this._default = _default;
        this._enum = _enum;
    }

    public String getDefault() {
        return _default;
    }

    public void setDefault(String _default) {
        this._default = _default;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEnum() {
        return _enum;
    }

    public void setEnum(List<String> _enum) {
        this._enum = _enum;
    }
}
