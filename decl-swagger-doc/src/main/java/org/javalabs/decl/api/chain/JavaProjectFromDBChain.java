package org.javalabs.decl.api.chain;

import org.javalabs.decl.workflow.AbstractChain;
import org.javalabs.decl.workflow.ChainConfig;

/**
 * A vert.x project chain.
 * 
 * <p>
 * Vert. x is a toolkit used for building reactive applications on the JVM using an asynchronous
 * and non-blocking execution model. As it is based on Netty, which is an event-driven and asynchronous
 * network application framework, Vert.
 * 
 * <p>
 * Vert.x is useful for creating asynchronous rest-based application. The setup is quick and easy.
 *
 * @author Sudiptasish Chanda
 */
public class JavaProjectFromDBChain extends AbstractChain {

    public JavaProjectFromDBChain(ChainConfig cc) {
        super(cc);
    }
}
