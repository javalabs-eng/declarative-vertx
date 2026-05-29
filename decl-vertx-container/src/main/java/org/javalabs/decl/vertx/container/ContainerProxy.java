package org.javalabs.decl.vertx.container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.javalabs.decl.container.Container;

/**
 * A proxy class for invoking the container life cycle.
 * 
 * <p>
 * This proxy class acts as a surrogate or placeholder for another container, known as the real 
 * or subject object. It provides an interface to the real object, allowing you to control access 
 * to the container and add extra functionality before or after method calls. 
 * This is a common implementation of the Proxy design pattern.
 *
 * @author Sudiptasish Chanda
 */
public class ContainerProxy implements InvocationHandler {
    
    private final Object container;

    public static Object newInstance(Object container) {
        Object obj = Proxy.newProxyInstance(
            container.getClass().getClassLoader(),
            new Class[] { Container.class },
            new ContainerProxy(container));
        
        return obj;
    }

    private ContainerProxy(Object container) {
        this.container = container;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            method.invoke(container, args);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
        return null;
    }
}
