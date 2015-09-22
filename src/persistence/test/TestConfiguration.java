package persistence.test;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import persistence.PersistenceUtil;
import persistence.configuration.AbstractPersistentProperty;
import persistence.configuration.PersistenceConfiguration;
import persistence.configuration.PersistentClass;
import persistence.configuration.SimpleColumnProperty;
import persistence.exception.PersistenceException;
import persistence.test.entity.Book;

public class TestConfiguration extends TestCase {
	
	private PersistenceConfiguration configuration;

	@Before
	public void setUp() throws PersistenceException {
		configuration = PersistenceUtil.getConfiguration();
	}

	@Test
	public void testConfiguration() throws Exception {
		for (PersistentClass pc : configuration.getPersistentClasses()) {
			System.out.println("--------------------------");
			System.out.println(pc.getName());
			System.out.println(pc.getTableName());
			for (AbstractPersistentProperty property : pc.getPersistentProperties()) {
				System.out.println(property.getName());
				System.out.println(property.getColumnName());
				System.out.println(property.getClazz());
				System.out.println(property.getSqlType());
				System.out.println(property.getColumnName());
			}
			System.out.println("--------------------------");
		}

		PersistentClass pcb = configuration.getPersistentClass(Book.class);
		assertNotNull(pcb);
		assertEquals(pcb.getTableName(), "book");
		AbstractPersistentProperty ppb = pcb.getPersistentProperty("id");
		SimpleColumnProperty columnProperty = (SimpleColumnProperty) ppb;
		assertTrue(columnProperty.isId());
		assertEquals(ppb, pcb.getId());
		ppb = pcb.getPersistentProperty("title");
		assertNotNull(ppb);
		assertEquals(ppb.getColumnName(), "title");
		assertEquals(ppb.getSqlType(), "VARCHAR(255)");
		
		
	}
}
