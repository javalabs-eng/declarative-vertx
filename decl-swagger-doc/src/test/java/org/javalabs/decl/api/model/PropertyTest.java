package org.javalabs.decl.api.model;

import java.io.IOException;
import java.util.Arrays;
import org.javalabs.decl.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

/**
 *
 * @author Sudiptasish Chanda
 */
public class PropertyTest {

    @Test
    public void testSerialize() throws IOException {
        Property prop = new Property();
        prop.setType("string");
        prop.set_enum(Arrays.asList("ACTIVE", "BLOCKED"));
        
        System.out.println(new String(MapperUtil.encode(prop)));
        System.out.println(MapperUtil.ymlMapper().writeValueAsString(prop));
        
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        // options.setIndicatorIndent(1);

        Representer representer = new YamlRepresenter(options);
        representer.addClassTag(OpenApiModel.class, org.yaml.snakeyaml.nodes.Tag.MAP);
        representer.addClassTag(Reference.class, org.yaml.snakeyaml.nodes.Tag.MAP);
        representer.addClassTag(Schema.class, org.yaml.snakeyaml.nodes.Tag.MAP);

        Yaml yaml = new Yaml(representer, options);
        String result = yaml.dump(prop);
        System.out.println(result);
    }
}
