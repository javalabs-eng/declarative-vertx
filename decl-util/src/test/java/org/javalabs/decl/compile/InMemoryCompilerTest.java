package org.javalabs.decl.compile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class InMemoryCompilerTest {

    @Test
    public void testCompile() {
        final String SOURCE_CODE
                = "package org.javalabs.model;\n\n"
                + "import java.io.Serializable;\n"
                + "import java.sql.Timestamp;\n\n"
                + "import java.util.Objects;\n"
                + "public class Department {\n"
                + "    \n"
                + "    private Integer id;\n"
                + "    private String name;\n"
                + "    private Timestamp createdOn;\n"
                + "\n"
                + "    public Integer getId() {\n"
                + "        return id;\n"
                + "    }\n"
                + "\n"
                + "    public void setId(Integer id) {\n"
                + "        this.id = id;\n"
                + "    }\n"
                + "\n"
                + "    public String getName() {\n"
                + "        return name;\n"
                + "    }\n"
                + "\n"
                + "    public void setName(String name) {\n"
                + "        this.name = name;\n"
                + "    }\n"
                + "\n"
                + "    public Timestamp getCreatedOn() {\n"
                + "        return createdOn;\n"
                + "    }\n"
                + "\n"
                + "    public void setCreatedOn(Timestamp createdOn) {\n"
                + "        this.createdOn = createdOn;\n"
                + "    }\n"
                + "    \n"
                + "    public static class DepartmentPK {\n"
                + "        private Integer id;\n"
                + "\n"
                + "        public DepartmentPK(Integer id) {\n"
                + "            this.id = id;\n"
                + "        }\n"
                + "\n"
                + "        public Integer getId() {\n"
                + "            return id;\n"
                + "        }\n"
                + "\n"
                + "        public void setId(Integer id) {\n"
                + "            this.id = id;\n"
                + "        }\n"
                + "\n"
                + "        @Override\n"
                + "        public int hashCode() {\n"
                + "            int hash = 3;\n"
                + "            hash = 53 * hash + Objects.hashCode(this.id);\n"
                + "            return hash;\n"
                + "        }\n"
                + "\n"
                + "        @Override\n"
                + "        public boolean equals(Object obj) {\n"
                + "            if (this == obj) {\n"
                + "                return true;\n"
                + "            }\n"
                + "            if (obj == null) {\n"
                + "                return false;\n"
                + "            }\n"
                + "            if (getClass() != obj.getClass()) {\n"
                + "                return false;\n"
                + "            }\n"
                + "            final DepartmentPK other = (DepartmentPK) obj;\n"
                + "            return Objects.equals(this.id, other.id);\n"
                + "        }\n"
                + "    }\n"
                + "}\n"
                + "";

        try {
            // Compile sample class.
            CompiledClassHolder holder = new CompiledClassHolder();
            Boolean success = InMemoryCompiler.compile(new String[]{"Department.java"}, new String[]{SOURCE_CODE}, holder);
            
            Assertions.assertTrue(success);
            
            // Now load them into memory.
            MemoryClassLoader memLoader = new MemoryClassLoader(getClass().getClassLoader(), holder);
            Class<?> clazz = memLoader.loadClass("org.javalabs.model.Department");
            
            Object obj = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getDeclaredMethod("setId", Integer.class);
            method.invoke(obj, 101);
            
            method = clazz.getDeclaredMethod("getId");
            Object val = method.invoke(obj);
            Assertions.assertEquals(101, (Integer)val);
        }
        catch (IOException | ClassNotFoundException | NoSuchMethodException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }
}
