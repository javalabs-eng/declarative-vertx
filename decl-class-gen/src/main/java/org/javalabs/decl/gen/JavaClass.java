package org.javalabs.decl.gen;

import org.javalabs.decl.util.DtypeUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.javalabs.decl.gen.decorator.EqualsDecorator;
import org.javalabs.decl.gen.decorator.HashCodeDecorator;
import org.javalabs.decl.gen.decorator.LineDecorator;
import org.javalabs.decl.visitor.ClassVisitor;
import org.javalabs.decl.visitor.Element;

/**
 * Representation of a java file.
 * 
 * <p>
 * A Java source file is a file (with the .java filename extension) containing the following details:
 * <ul>
 *   <li>The package name</li>
 *   <li>The set of import statements</li>
 *   <li>Name of the class</li>
 *   <li>Variable names</li>
 *   <li>Set of methods</li>
 * </ul>
 * 
 * <p>
 * Client can call the appropriate visitor method which will generate the complete java file from the information
 * provided. Client has to ensure the mandatory attributes are populated before calling the appropriate visitor method
 * , which will instantly generate valid java code based on specific tasks.
 *
 * @author Sudiptasish Chanda
 */
public class JavaClass implements CodeGenSupport, Element {
    
    public static enum TYPE {CLASS, INTERFACE, ENUM};
    
    public static final int CONSTRUCTOR_NO_ARG = 0;
    public static final int CONSTRUCTOR_PARAMETERIZED = 1;
    public static final int CONSTRUCTOR_BOTH = 2;
    
    private JavaClass outer;
    
    private final String name;
    private final TYPE cType;
    private String dir;
    
    // For anum type
    private List<String> values;
    
    private JavaComment comment;
    private JavaPackage jPackage;
    private String accessSpecifier = "public";
    private String modifier;
    private Class<?> parentClass;
    private final List<Class<?>> parentInterfaces = new ArrayList<>();
    
    // Flag to indicate whether constructor to be auto generated.
    private int constructorType = CONSTRUCTOR_NO_ARG;
    private Boolean autoGenConstructor = Boolean.FALSE;
    private final List<JavaConstructor> jCons = new ArrayList<>();
    
    private final List<JavaImport> jImports = new ArrayList<>();
    private final List<JavaAnnotation> jAnnotations = new ArrayList<>();
    private final List<JavaVariable> jVars = new ArrayList<>();
    
    private Boolean autoGenMethod = Boolean.FALSE;
    private final List<JavaMethod> jMethods = new ArrayList<>();
    
    private Boolean autoGenHashCode = Boolean.FALSE;
    private Boolean autoGenEquals = Boolean.FALSE;
    
    private Boolean frozen = Boolean.FALSE;
    
    private List<JavaClass> inners = new ArrayList<>();
    private LineDecorator decorator;
    
    public JavaClass(String name) {
        this(name, TYPE.CLASS, new LineDecorator());
    }
    
    public JavaClass(String name, TYPE cType) {
        this(name, cType, new LineDecorator());
    }
    
    public JavaClass(String name, LineDecorator decorator) {
        this(name, TYPE.CLASS, decorator);
    }
    
    public JavaClass(String name, TYPE cType, LineDecorator decorator) {
        this.name = name;
        this.cType = cType;
        this.decorator = decorator;
    }
    
    public void setDecorator(LineDecorator decorator) {
        this.decorator = decorator;
    }
    
    public LineDecorator decorator() {
        return this.decorator;
    }
    
    public void pkg(JavaPackage jPackage) {
        this.jPackage = jPackage;
    }
    
    public JavaPackage pkg() {
        return this.jPackage;
    }
    
    public void comment(JavaComment comment) {
        this.comment = comment;
    }

    public void accessSpecifier(String accessSpecifier) {
        this.accessSpecifier = accessSpecifier;
    }

    public void modifier(String modifier) {
        this.modifier = modifier;
    }

    public void addImport(JavaImport jImport) {
        if (! jImports.contains(jImport)) {
            this.jImports.add(jImport);
        }
    }

    public void setParentClass(Class<?> parentClass) {
        if (parentClass.isInterface()) {
            throw new IllegalArgumentException(parentClass + " is not a class");
        }
        this.parentClass = parentClass;
        JavaImport jImport = new JavaImport(parentClass);
        String pkgName = parentClass.getName().substring(0, parentClass.getName().lastIndexOf("."));
        if (! this.jImports.contains(jImport) && ! pkgName.equals("java.lang")
                && jPackage == null || (jPackage != null && ! jPackage.equals(new JavaPackage(pkgName)))) {
            this.jImports.add(new JavaImport(parentClass));
        }
    }

    public void addParentInterface(Class<?> parentInterface) {
        if (! parentInterface.isInterface()) {
            throw new IllegalArgumentException(parentInterface + " is not an interface");
        }
        if (! parentInterfaces.contains(parentInterface)) {
            this.parentInterfaces.add(parentInterface);
            
            JavaImport jImport = new JavaImport(parentInterface);
            String pkgName = parentInterface.getName().substring(0, parentInterface.getName().lastIndexOf("."));
            if (! this.jImports.contains(jImport) && ! pkgName.equals("java.lang")
                    && jPackage == null || (jPackage != null && ! jPackage.equals(new JavaPackage(pkgName)))) {
                this.jImports.add(new JavaImport(parentInterface));
            }
        }
    }
    
    public void addAnnotation(JavaAnnotation ann) {
        if (! jAnnotations.contains(ann)) {
            jAnnotations.add(ann);
        }
    }
    
    public void addVar(JavaVariable var) {
        if (! jVars.contains(var)) {
            jVars.add(var);
        }
    }
    
    public void addCons(JavaConstructor cons) {
        jCons.add(cons);
    }

    public void addMethod(JavaMethod jMethod) {
        this.jMethods.add(jMethod);
    }
    
    public void inner(JavaClass inner) {
        inner.modifier("static");
        inner.setDecorator(new LineDecorator(this.decorator.depth() + 1));
        this.inners.add(inner);
        
        inner.outer = this;
    }
    
    public void dir(String dir) {
        this.dir = dir;
    }
    
    public String dir() {
        return dir;
    }
    
    public String name() {
        return name;
    }
    
    public TYPE ctype() {
        return cType;
    }
    
    public void values(String... vals) {
        if (values == null) {
            values = new ArrayList<>();
        }
        if (vals != null) {
            for (String val : vals) {
                values.add(val);
            }
        }
    }
    
    public List<JavaAnnotation> annotations() {
        return Collections.unmodifiableList(jAnnotations);
    }
    
    public List<JavaVariable> variables() {
        return Collections.unmodifiableList(jVars);
    }
    
    public List<JavaConstructor> constructors() {
        return Collections.unmodifiableList(jCons);
    }
    
    public List<JavaMethod> methods() {
        return Collections.unmodifiableList(jMethods);
    }

    public void autoGenConstructor(Boolean autoGenConstructor, Integer constructorType) {
        this.autoGenConstructor = autoGenConstructor;
        this.constructorType = constructorType;
    }

    public void autoGenMethod(Boolean autoGenMethod) {
        this.autoGenMethod = autoGenMethod;
    }

    public void autoGenHashCode(Boolean autoGenHashCode) {
        this.autoGenHashCode = autoGenHashCode;
    }

    public void autoGenEquals(Boolean autoGenEquals) {
        this.autoGenEquals = autoGenEquals;
    }
    
    public String idDataType() {
        for (JavaVariable jVar : jVars) {
            if (jVar.idField()) {
                return jVar.typeName();
            }
        }
        return "String";       // Default data type for an id.
    }
    
    public String table() {
        try {
            Class<?> clazz = Class.forName("jakarta.persistence.Table");
            for (JavaAnnotation jAnn : jAnnotations) {
                if (jAnn.type() == clazz) {
                    return (String)jAnn.prop("name");
                }
            }
            return "__not_found__";
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void freeze() {
        this.frozen = Boolean.TRUE;
        
        // Check if the import statements
//        for (JavaAnnotation jAnn : jAnnotations) {
//            String pkgName = jAnn.type().getName().substring(0, jAnn.type().getName().lastIndexOf("."));
//            if (! pkgName.equals("java.lang")) {
//                JavaImport jImport = new JavaImport(jAnn.type());
//                if (! jImports.contains(jImport)) {
//                    jImports.add(jImport);
//                }
//            }
//            if (jAnn.children() != null) {
//                for (JavaAnnotation childAnn : jAnn.children()) {
//                    pkgName = childAnn.type().getName().substring(0, jAnn.type().getName().lastIndexOf("."));
//                    if (! pkgName.equals("java.lang")) {
//                        JavaImport jImport = new JavaImport(childAnn.type());
//                        if (! jImports.contains(jImport)) {
//                            jImports.add(jImport);
//                        }
//                    }
//                }
//            }
//        }
        for (JavaVariable jVar : jVars) {
            if (jVar.typeName().startsWith("[")) {
                // It's an array (probably [Ljava.lang.String; , or [B, etc)
                // ignore it.
                continue;
            }
            if ( jVar.type() != null) {
                if (! jVar.type().getPackageName().equals("java.lang") && ! DtypeUtil.isPrimitive(jVar.type())) {
                    JavaImport jImport = new JavaImport(jVar.type());
                    if (! jImports.contains(jImport)) {
                        jImports.add(jImport);
                    }
                }
            }
            // Check the annotation inside java variable.
//            if (jVar.annotations() != null) {
//                for (JavaAnnotation varAnn : jVar.annotations()) {
//                    pkgName = varAnn.type().getName().substring(0, varAnn.type().getName().lastIndexOf("."));
//                    if (! pkgName.equals("java.lang")) {
//                        JavaImport jImport = new JavaImport(varAnn.type());
//                        if (! jImports.contains(jImport)) {
//                            jImports.add(jImport);
//                        }
//                    }
//                }
//            }
        }
        
        if (autoGenConstructor) {
            switch (constructorType) {
                case CONSTRUCTOR_NO_ARG:
                    jCons.add(new JavaConstructor(this));
                    break;
                case CONSTRUCTOR_PARAMETERIZED:
                    jCons.add(JavaConstructor.parameterized(this));
                    break;
                case CONSTRUCTOR_BOTH:
                    jCons.add(new JavaConstructor(this));
                    jCons.add(JavaConstructor.parameterized(this));
                    break;
                default:
                    break;
            }
        }
        if (autoGenMethod) {
            if (! jVars.isEmpty()) {
                for (int i = 0; i < jVars.size(); i ++) {
                    JavaVariable jVar = jVars.get(i);
                    jMethods.add(JavaMethod.setter(this, jVar));
                    jMethods.add(JavaMethod.getter(this, jVar));
                }
            }
        }
        // Arrange the import statementsin sorted order.
        Collections.sort(jImports, (JavaImport o1, JavaImport o2) -> o1.toString().compareTo(o2.toString()));
    }
    
    @Override
    public String snippet() {
        if (! this.frozen) {
            throw new IllegalStateException("After setting necessary attributes, call JavaClass.freeze(), before generating the code");
        }
        StringBuilder buff = new StringBuilder(512);
        if (jPackage != null) {
            buff.append(jPackage.snippet());
            buff.append(NEW_LINE).append(NEW_LINE);
        }
        if (outer == null && ! jImports.isEmpty()) {
            for (JavaImport jImport : jImports) {
                buff.append(jImport.snippet());
                buff.append(NEW_LINE);
            }
            buff.append(NEW_LINE).append(NEW_LINE);
        }
        if (comment != null) {
            buff.append(comment.snippet());
            buff.append(NEW_LINE).append(NEW_LINE);
        }
        if (! jAnnotations.isEmpty()) {
            for (JavaAnnotation ann : jAnnotations) {
                buff.append(ann.snippet())
                        .append(NEW_LINE);
            }
        }
        String typeName = "class";
        if (cType == TYPE.INTERFACE) {
            typeName = "interface";
        }
        else if (cType == TYPE.ENUM) {
            typeName = "enum";
        }
        buff.append(decorator.classIndent())
                .append(accessSpecifier)
                .append(SPACE)
                .append(modifier != null ? (modifier + SPACE) : "")
                .append(typeName)
                .append(SPACE)
                .append(name)
                .append(SPACE);
        
        if (parentClass != null) {
            buff.append("extends")
                    .append(SPACE)
                    .append(parentClass.getSimpleName())
                    .append(SPACE);
        }
        if (parentInterfaces != null && ! parentInterfaces.isEmpty()) {
            buff.append("implements");
            for (int i = 0; i < parentInterfaces.size(); i ++) {
                buff.append(SPACE).append(parentInterfaces.get(i).getSimpleName());
                if (i < parentInterfaces.size() - 1) {
                    buff.append(COMMA);
                }
            }
            buff.append(SPACE);
        }
        buff.append("{");
        if (cType == TYPE.ENUM) {
            buff.append(NEW_LINE);
        }
        else {
            buff.append(NEW_LINE).append(NEW_LINE);
        }
        
        // Among the inner classes, enum classes will be written first (before variable declaration)
        if (! inners.isEmpty()) {
            for (JavaClass inner : inners) {
                if (inner.ctype() == TYPE.ENUM) {
                    buff.append(inner.snippet());
                    buff.append(SEMICOLON);
                    buff.append(NEW_LINE).append(NEW_LINE);
                }
            }
        }
        
        if (cType == TYPE.ENUM) {
            for (int i = 0; i < values.size(); i ++) {
                buff.append(decorator().variableIndent()).append(values.get(i));
                if (i < values.size() - 1) {
                    buff.append(COMMA);
                }
                else {
                    buff.append(SEMICOLON);
                }
                buff.append(NEW_LINE);
            }
        }
        
        if (! jVars.isEmpty()) {
            for (JavaVariable jVar : jVars) {
                buff.append(jVar.snippet());
                buff.append(NEW_LINE).append(NEW_LINE);
            }
        }
        if (! jCons.isEmpty()) {
            for (JavaConstructor jCon : jCons) {
                buff.append(jCon.snippet());
                buff.append(NEW_LINE).append(NEW_LINE);
            }
        }
        for (JavaMethod jMethod : jMethods) {
            buff.append(jMethod.snippet());
            buff.append(NEW_LINE).append(NEW_LINE);
        }
        if (autoGenHashCode) {
            buff.append(new HashCodeDecorator().genHashCode(this));
            buff.append(NEW_LINE).append(NEW_LINE);
        }
        if (autoGenEquals) {
            buff.append(new EqualsDecorator().genEquals(this));
            buff.append(NEW_LINE).append(NEW_LINE);
        }
        
        // Check inner class (and ONLY classes).
        if (inners != null && ! inners.isEmpty()) {
            for (JavaClass inner : inners) {
                if (inner.ctype() == TYPE.CLASS) {
                    buff.append(inner.snippet());
                }
            }
        }
        
        // End of class
        if (outer == null) {
            buff.append(NEW_LINE);
        }
        buff.append(decorator.classIndent())
                .append("}");
                //.append(NEW_LINE);
        
        return buff.toString();
    }

    @Override
    public void accept(ClassVisitor visitor) {
        visitor.visit(this);
    }
}
