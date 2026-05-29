package org.javalabs.decl.gen;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.sql.Timestamp;
import org.javalabs.orm.jaxb.AttributesType;
import org.javalabs.orm.jaxb.BasicType;
import org.javalabs.orm.jaxb.ColumnType;
import org.javalabs.orm.jaxb.EntityType;
import org.javalabs.orm.jaxb.EntityMappingsType;
import org.javalabs.orm.jaxb.IdType;
import org.javalabs.orm.jaxb.NamedNativeQueriesType;
import org.javalabs.orm.jaxb.NamedNativeQueryType;
import org.javalabs.orm.jaxb.TableType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class ORMTest {
    
    @Test
    public void testGenerateOrmXML() {
        try {
            EntityMappingsType mapping = new EntityMappingsType();
            EntityType entity = new EntityType();

            TableType table = new TableType();
            table.setName("employee_hist");
            
            NamedNativeQueryType nnQuery = new NamedNativeQueryType();
            nnQuery.setName("Employee.selectAll");
            nnQuery.setQuery("SELECT * FROM employee_hist");

            NamedNativeQueriesType nnQueries = new NamedNativeQueriesType();
            nnQueries.getNamedNativeQuery().add(nnQuery);

            AttributesType attrs = new AttributesType();

            // Add primary key attributes.
            IdType id = new IdType();
            id.setName("empId");
            id.setType(String.class.getName());

            ColumnType col = new ColumnType();
            col.setName("emp_id");
            col.setLength(32);

            id.setColumn(col);
            attrs.getId().add(id);

            id = new IdType();
            id.setName("endDate");
            id.setType(Timestamp.class.getName());

            col = new ColumnType();
            col.setName("end_date");

            id.setColumn(col);
            attrs.getId().add(id);

            // Add other columns attributes.
            BasicType basic = new BasicType();
            basic.setName("locationName");
            basic.setType(String[].class.getName());

            col = new ColumnType();
            col.setName("location_name");
            col.setLength(256);
            col.setNullable("false");
            col.setInsertable("true");
            col.setInsertable("false");

            basic.setColumn(col);
            attrs.getBasic().add(basic);

            basic = new BasicType();
            basic.setName("itemrice");
            basic.setType(String.class.getName());

            col = new ColumnType();
            col.setName("item_price");
            col.setNullable("false");
            col.setInsertable("true");
            col.setInsertable("true");
            col.setPrecision(10);
            col.setScale(2);

            basic.setColumn(col);
            attrs.getBasic().add(basic);

            entity.setTable(table);
            entity.setNamedNativeQueries(nnQueries);
            entity.setAttributes(attrs);

            mapping.setPackage("org.javalabs.decl.gen.orm");
            mapping.getEntity().add(entity);

            JAXBContext context = JAXBContext.newInstance(EntityMappingsType.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Output to console
            marshaller.marshal(mapping, System.out);
            
            
        }
        catch (JAXBException e) {
            Assertions.fail(e);
        }
    }
}
