package org.javalabs.decl.gen;

import java.util.Objects;
import org.javalabs.decl.visitor.ClassVisitor;
import org.javalabs.decl.visitor.Element;

/**
 * Representation of a java import statement.
 *
 * @author Sudiptasish Chanda
 */
public class JavaImport implements CodeGenSupport, Element {
    
    private Class<?> importClass;
    
    public JavaImport() {}
    
    public JavaImport(Class<?> importClass) {
        this.importClass = importClass;
    }
    
    public void set(Class<?> im) {
        this.importClass = im;
    }

    @Override
    public int hashCode() {
        return importClass.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JavaImport other = (JavaImport) obj;
        return Objects.equals(this.importClass, other.importClass);
    }
    
    @Override
    public String snippet() {
        return "import " + importClass.getName() + ";";
    }

    @Override
    public void accept(ClassVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return importClass.getName();
    }
}
