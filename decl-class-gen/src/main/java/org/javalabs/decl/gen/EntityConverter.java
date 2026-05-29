package org.javalabs.decl.gen;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.javalabs.decl.util.CharUtil;
import org.javalabs.orm.jaxb.AttributesType;
import org.javalabs.orm.jaxb.BasicType;
import org.javalabs.orm.jaxb.ColumnType;
import org.javalabs.orm.jaxb.EntityType;
import org.javalabs.orm.jaxb.IdType;
import org.javalabs.orm.jaxb.NamedNativeQueryType;

/**
 *
 * @author Sudiptasish Chanda
 */
public class EntityConverter {

    private static final String AUTO_GEN_COMMENT = "This class is auto generated with jpa-lite framework.";
    private static final String AUTHOR = "Sudiptasish Chanda";
    
    public JavaClass toJavaClass(EntityType entity, String pkgName) throws ClassNotFoundException {
        JavaClass jClass = new JavaClass(entity.getName());
        jClass.addParentInterface(Serializable.class);
        jClass.addParentInterface(Cloneable.class);
        jClass.comment(new JavaComment(jClass).comment(AUTO_GEN_COMMENT).author(AUTHOR));

        jClass.pkg(new JavaPackage(pkgName));
        jClass.addImport(new JavaImport(Objects.class));
        jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.Entity")));
        jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.Table")));
        jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.Column")));

        jClass.autoGenConstructor(Boolean.TRUE, JavaClass.CONSTRUCTOR_NO_ARG);

        List<JavaAnnotation> anns = new ArrayList<>();
        anns.add(new JavaAnnotation(jClass).typeName("jakarta.persistence.Entity"));
        anns.add(new JavaAnnotation(jClass)
                .typeName("jakarta.persistence.Table")
                .props(new LinkedHashMap<>() {{ put("name", entity.getTable().getName()); }}));

        if (entity.getAttributes().getId() != null && ! entity.getAttributes().getId().isEmpty()) {
            anns.add(new JavaAnnotation(jClass)
                    .typeName("jakarta.persistence.IdClass")
                    .props(new LinkedHashMap<>() {{ put("value", jClass.name() + "." + jClass.name() + "PK.class"); }}));

            jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.IdClass")));
        }
        if (entity.getNamedNativeQueries() != null) {
            JavaAnnotation nqAnns = new JavaAnnotation(jClass).typeName("jakarta.persistence.NamedNativeQueries");
            List<JavaAnnotation> childAnns = new ArrayList<>(2);

            for (NamedNativeQueryType nnQuery : entity.getNamedNativeQueries().getNamedNativeQuery()) {
                childAnns.add(
                        new JavaAnnotation(jClass)
                            .typeName("jakarta.persistence.NamedNativeQuery")
                            .props(new LinkedHashMap<>() {{ put("name", nnQuery.getName()); put("query", nnQuery.getQuery()); }}));
            }
            nqAnns.children(childAnns.toArray(new JavaAnnotation[childAnns.size()]));
            anns.add(nqAnns);

            // Add the required import(s).
            jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.NamedNativeQueries")));
            jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.NamedNativeQuery")));
        }
        for (JavaAnnotation jAnn : anns) {
            jClass.addAnnotation(jAnn);
        }
        AttributesType attrs = entity.getAttributes();
        List<IdType> ids = attrs.getId();
        
        for (IdType id : ids) {
            List<JavaAnnotation> colAnns = new ArrayList<>();
            
            colAnns.add(new JavaAnnotation(jClass).typeName("jakarta.persistence.Id"));
            if (id.getType() == null || id.getType().trim().length() == 0) {
                // Check generated tag.
                if (id.getGeneratedValue() == null) {
                    throw new IllegalArgumentException("Cannot determine data type for id column."
                            + " Either type attribute is missing, or generated-value tag is missing.");
                }
                // generated-value is present. Therefore, consider it as an integer type.
                id.setType("java.lang.Integer");
                
                // It is a generated attribute. The java field name and column name is same.
                ColumnType col = new ColumnType();
                col.setName(id.getName());
                id.setColumn(col);
                
            }
            Class<?> clazz = Class.forName(id.getType());
            
            if (id.getGeneratedValue() != null) {
                Class<?> enumClazz = Class.forName("jakarta.persistence.GenerationType");
                Object enumVal = null;

                for (Object cons : enumClazz.getEnumConstants()) {
                    if (((Enum)cons).name().equalsIgnoreCase("IDENTITY")) {
                        enumVal = cons;
                        break;
                    }
                }
                final Object ev = enumVal;
                colAnns.add(new JavaAnnotation(jClass)
                        .typeName("jakarta.persistence.GeneratedValue")
                        .props(new LinkedHashMap<>() {{ put("strategy", ev); }}));

                jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.GeneratedValue")));
                jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.GenerationType")));
            }
            jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.Id")));
            
            // Not prepare to extract the basic column definition.
            LinkedHashMap<String, Object> props = columnAttributes(id.getName(), id.getColumn(), clazz);
            colAnns.add(new JavaAnnotation(jClass).typeName("jakarta.persistence.Column").props(props));
            jClass.addVar(new JavaVariable(jClass)
                    .type(clazz)
                    .name(id.getName())
                    .annotations(colAnns)
                    .idField(Boolean.TRUE));
        }

        // Add non identity table column(s).
        outer:
        for (BasicType basic : attrs.getBasic()) {
            if (basic.getType() == null || basic.getType().trim().length() == 0) {
                throw new IllegalArgumentException("Cannot determine data type for id column. type attribute is missing.");
            }
            for (IdType id : ids) {
                if (basic.getName().equals(id.getName())) {
                    continue outer;
                }
            }
            Class<?> clazz = Class.forName(basic.getType());
            if (clazz == Enum.class) {
                String expr = basic.getColumn().getCheck();
                String opts = "";
                
                if (expr != null && expr.trim().length() > 0) {
                    // check="status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')"
                    opts = expr.substring(expr.indexOf("("));
                }
                else {
                    opts = basic.getValue();
                }
                if (opts == null || opts.trim().length() == 0) {
                    throw new IllegalArgumentException("Must provide value attribute for Enum type. Example: value=\"(RECEIVED, PENDING, COMPLETE)\"");
                }
                if (opts.charAt(0) != '(' || opts.charAt(opts.length() - 1) != ')') {
                    throw new IllegalArgumentException("Invalid format. Correct format: (RECEIVED, PENDING, COMPLETE)");
                }
                opts = opts.substring(1, opts.length() - 1);
                if (opts.trim().length() == 0) {
                    throw new IllegalArgumentException("Empty array provided for enum type. Example: value=\"(RECEIVED, PENDING, COMPLETE)\"");
                }
                String[] vals = opts.split(",");
                for (int i = 0; i < vals.length; i ++) {
                    vals[i] = vals[i].replace("'", "").trim();
                }
                // Create the enum class.
                JavaClass enumClass = new JavaClass(CharUtil.toCapitalisedCamelCase(basic.getName()), JavaClass.TYPE.ENUM);
                enumClass.values(vals);
                enumClass.freeze();
                jClass.inner(enumClass);
                
                jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.EnumType")));
                jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.Enumerated")));
                jClass.addImport(new JavaImport(Class.forName("jakarta.persistence.CheckConstraint")));
            }
            List<JavaAnnotation> colAnns = new ArrayList<>(1);
            
            LinkedHashMap<String, Object> props = columnAttributes(basic.getName(), basic.getColumn(), clazz);
            JavaAnnotation colAnn = new JavaAnnotation(jClass).typeName("jakarta.persistence.Column").props(props);
            
            colAnns.add(colAnn);
            
            if (clazz == Enum.class) {
                colAnns.add(new JavaAnnotation(jClass)
                        .typeName("jakarta.persistence.Enumerated")
                        .props(new LinkedHashMap<>() {{ put("value", "EnumType.STRING"); }}));
                
                jClass.addVar(new JavaVariable(jClass)
                        .typeName(pkgName + "." + entity.getName() + "." + CharUtil.toCapitalisedCamelCase(basic.getName()))
                        .name(basic.getName())
                        .annotations(colAnns));
            }
            else {
                jClass.addVar(new JavaVariable(jClass)
                        .type(clazz)
                        .name(basic.getName())
                        .annotations(colAnns));
            }
            
        }
        // Auto generate setters and getters
        jClass.autoGenMethod(Boolean.TRUE);

        // Prepare the inner PK class.
        if (ids != null && ! ids.isEmpty()) {
            JavaClass inner = new JavaClass(jClass.name() + "PK");
            jClass.inner(inner);

            for (IdType id : ids) {
                inner.addVar(new JavaVariable(inner)
                        .type(Class.forName(id.getType()))
                        .name(id.getName()));
            }
            inner.autoGenConstructor(Boolean.TRUE, JavaClass.CONSTRUCTOR_BOTH);
            inner.autoGenMethod(Boolean.TRUE);
            inner.autoGenHashCode(Boolean.TRUE);
            inner.autoGenEquals(Boolean.TRUE);
            inner.freeze();
        }
        return jClass;
    }
    
    private LinkedHashMap columnAttributes(String name, ColumnType col, Class<?> colDataType) throws ClassNotFoundException {
        LinkedHashMap<String, Object> props = new LinkedHashMap<>();

        props.put("name", col != null ? col.getName() : name);
        props.put("nullable", col != null ? Boolean.valueOf(col.getNullable()) : false);
        props.put("updatable", col != null ? Boolean.valueOf(col.getUpdatable()) : false);
        
        if (col != null) {
            if (col.getCheck() != null) {
                JavaAnnotation cAnn = new JavaAnnotation(null)
                        .typeName("jakarta.persistence.CheckConstraint")
                        .props(new LinkedHashMap<>() {{ put("constraint", col.getCheck()); }});
                props.put("check", cAnn);
            }
            if (colDataType == String.class) {
                props.put("length", col.getLength());
            }
            else if (colDataType == Float.class || colDataType == Double.class || colDataType == BigDecimal.class) {
                props.put("precision", col.getPrecision());
                props.put("scale", col.getScale());
            }
            else if (colDataType == Byte.class || colDataType == Short.class
                    || colDataType == Integer.class || colDataType == Long.class || colDataType == BigInteger.class) {
                props.put("precision", col.getPrecision());
            }
        }
        return props;
    }
}
