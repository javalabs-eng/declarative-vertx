package org.javalabs.decl.workflow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.util.ObjectCreator;
import org.javalabs.decl.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The workflow initializer class.
 * 
 * <p>
 * This initializer class is a piece of code that is used to load and register the workflow engine and
 * all the chains with the centralized workflow management system. This is necessary in order to
 * load and register the workflow chains and their associated commands with the system catalog.
 *
 * @author Sudiptasish Chanda
 */
public class WorkflowInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowInitializer.class);
    
    private static final Integer DEFAULT_MAX_RETRY = 3;
    private static final String DEFAULT_CONFIG = "conf/workflow-config.xml";
    
    static {
        load();
    }
    
    /**
     * Method to load the workflow chains and their associated commands.
     * 
     * <p>
     * It looks for the workflow management configuration file, workflow-config.xml and initialize
     * the system commands.
     */
    private static void load() {
        try {
            Boolean verbose = Boolean.TRUE;
            String file = System.getProperty("workflow.confog", DEFAULT_CONFIG);

            Document doc = read(file);
            Element root = doc.getDocumentElement();
            
            // Read the info.
            NodeList info = root.getElementsByTagName("info");
            if (info.getLength() == 1) {
                Node chainNode = info.item(0);
                NamedNodeMap map = chainNode.getAttributes();
                
                if (map.getNamedItem("verbose") != null) {
                    String flag = map.getNamedItem("verbose").getTextContent();
                    verbose = Boolean.valueOf(flag);
                }
            }

            // Read the workflow engine class.
            NodeList children = root.getElementsByTagName("engine");
            if (children.getLength() >= 1) {
                Node node = children.item(0);
                String val = node.getTextContent();

                WorkflowEngine engine = ObjectCreator.create(val);
                engine.init();
                WorkflowEngine.set(engine);
            }

            NodeList chains = root.getElementsByTagName("chain");
            for (byte i = 0; i < chains.getLength(); i ++) {
                Node chainNode = chains.item(i);
                NamedNodeMap map = chainNode.getAttributes();

                // Extract the workflow chain name.
                String name = map.getNamedItem("name").getTextContent();
                String clazz = map.getNamedItem("class").getTextContent();
                Boolean async = Boolean.FALSE;
                if (map.getNamedItem("async") != null && map.getNamedItem("async").getTextContent().length() > 0) {
                    async = Boolean.valueOf(map.getNamedItem("async").getTextContent());
                }
                Integer maxRetry = DEFAULT_MAX_RETRY;
                if (map.getNamedItem("maxRetry") != null && map.getNamedItem("maxRetry").getTextContent().length() > 0) {
                    maxRetry = Integer.valueOf(map.getNamedItem("maxRetry").getTextContent());
                }
                ChainConfig cc = new ChainConfig(name, async, maxRetry);
                Chain chain = ObjectCreator.create(clazz, new Class[] {ChainConfig.class}, new Object[] {cc});

                // Now, extract the associated command(s).
                NodeList commands = ((Element)chainNode).getElementsByTagName("command");
                for (byte j = 0; j < commands.getLength(); j ++) {
                    Node commandNode = commands.item(j);
                    map = commandNode.getAttributes();

                    name = map.getNamedItem("name").getTextContent();
                    clazz = map.getNamedItem("class").getTextContent();
                    if (map.getNamedItem("order") != null) {
                        String order = map.getNamedItem("order").getTextContent();
                    }

                    // Add this command to the (parent) chain.
                    Command command = ObjectCreator.create(clazz, new Class[] {String.class}, new Object[] {name});
                    chain.add(command);
                }

                // All commands have been added to this chain.
                // Now, initialize the (parent) workflow chain and add it to the system catalog.
                chain.init();

                SystemCatalog.get().add(chain);
            }
            if (verbose && LOGGER.isInfoEnabled()) {
                LOGGER.info("Read workflow configuration file: {}", file);
                LOGGER.info(new String(MapperUtil.prettyWrite(SystemCatalog.get().getAll())));
            }
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    private static Document read(String workflowXml) {
        try {
            byte[] buff = StreamUtil.read(workflowXml);
            if (buff.length == 0) {
                throw new RuntimeException("Either the workflow config file [" + workflowXml + "] is not found or it is empty");
            }
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            Document doc = domBuilder.parse(new ByteArrayInputStream(buff));
            
            return doc;
        }
        catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
