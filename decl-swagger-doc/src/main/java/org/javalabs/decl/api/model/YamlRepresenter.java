package org.javalabs.decl.api.model;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.representer.Representer;

/**
 *
 * @author Sudiptasish Chanda
 */
public class YamlRepresenter extends Representer {

    public YamlRepresenter(DumperOptions options) {
        super(options);
        setPropertyUtils(new YamlPropertyUtils());
    }
    
    @Override
    protected NodeTuple representJavaBeanProperty(Object bean
            , Property property
            , Object val
            , org.yaml.snakeyaml.nodes.Tag tag) {

        // if value of property is null, ignore it.
        if (val == null) {
            return null;
        }  
        else {
            return super.representJavaBeanProperty(bean, property, val, tag);
        }
    }
}
