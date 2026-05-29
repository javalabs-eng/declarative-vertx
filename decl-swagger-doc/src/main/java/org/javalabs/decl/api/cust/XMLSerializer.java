package org.javalabs.decl.api.cust;

import org.javalabs.decl.vertx.jaxb.Mapping;
import org.javalabs.decl.vertx.jaxb.ResourceMapping;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;

/**
 *
 * @author Sudiptasish Chanda
 */
public class XMLSerializer {
    
    public static String serialize(RoutingConfig rc) {
        try {
            StringBuilder buff = new StringBuilder(2048);

            buff.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            buff.append("\n").append("<routing-config>");
            buff.append("\n").append("<api version=\"1.0\" basePath=\"/api/v1\" produce=\"application/vnd.ex+json.v1\" consume=\"application/json\"/>");

            for (ResourceMapping rm : rc.getResourceMapping()) {
                buff.append("\n").append("<resource-mapping")
                        .append(" name").append("=").append("\"").append(rm.getName()).append("\"")
                        .append(" path").append("=").append("\"").append(rm.getPath()).append("\"")
                        .append(" resource").append("=").append("\"").append(rm.getResource()).append("\"");

                if (rm.getSchema() != null) {
                    buff.append(" name").append("=").append("\"").append(rm.getName()).append("\"");
                }
                buff.append(">");

                for (Mapping m : rm.getMapping()) {
                    buff.append("\n").append("<mapping")
                            .append(" uri").append("=").append("\"").append(m.getUri()).append("\"")
                            .append(" method").append("=").append("\"").append(m.getMethod()).append("\"")
                            .append(" api").append("=").append("\"").append(m.getApi()).append("\"");

                    if (m.getSchema() != null) {
                        buff.append(" schema").append("=").append("\"").append(m.getSchema()).append("\"");
                    }
                    if (m.getConsume() != null) {
                        buff.append(" consume").append("=").append("\"").append(m.getConsume()).append("\"");
                    }
                    if (m.getProduce() != null) {
                        buff.append(" produce").append("=").append("\"").append(m.getProduce()).append("\"");
                    }
                    buff.append("/>");
                }
                buff.append("\n").append("</resource-mapping>");
            }
            buff.append("\n").append("</routing-config>");

            return buff.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
