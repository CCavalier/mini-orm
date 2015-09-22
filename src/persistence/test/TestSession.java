/**
 * 
 */
package persistence.test;

import java.math.BigDecimal;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import persistence.configuration.PersistenceConfiguration;
import persistence.criteria.Criteria;
import persistence.exception.PersistenceException;
import persistence.session.PersistenceSession;
import persistence.session.PersistenceSessionFactory;
import persistence.test.entity.Book;
import persistence.test.entity.Category;
import persistence.test.entity.Customer;
import persistence.test.entity.Order;


/**
 * @author dga
 * 
 */
public class TestSession extends TestCase {
	
	private PersistenceConfiguration configuration;
	private PersistenceSessionFactory factory;

	@Before
	public void setUp() throws PersistenceException {
		configuration = new PersistenceConfiguration("persistence/test/persistence-config-test.xml");
		factory = new PersistenceSessionFactory(configuration);
	}

	@Test
	public void testDropTable() throws Exception {
		PersistenceSession s = factory.openSession();
		try {
			s.dropTable(Order.class);
			s.dropTable(Customer.class);
			s.dropTable(Book.class);
			s.dropTable(Category.class);
		} catch (PersistenceException e) {
			fail();
		}
		s.close();
	}

	@Test
	public void testCreateTable() throws Exception {
		try {
			PersistenceSession s = factory.openSession();
			s.createTable(Category.class);
			s.createTable(Book.class);
			s.createTable(Customer.class);
			s.createTable(Order.class);
			s.close();
		} catch (PersistenceException e) {
			fail();
		}
	}

	@Test
	public void testSave() throws Exception {
		PersistenceSession s = factory.openSession();
		Book b = new Book();
		b.setTitle("Java");
		b.setPrice(new BigDecimal("1000.35"));
		Category c = new Category();
		c.setTitle("Informatique");
		s.save(c);
		assertNotNull(c.getId());
		b.setCategory(c);
		s.save(b);
		assertNotNull(b.getId());

		Book bb = (Book) s.get(Book.class, b.getId());

		assertEquals(b.getId(), bb.getId());
		assertEquals(b.getTitle(), bb.getTitle());
		assertEquals(b.getPrice(), bb.getPrice());
		s.close();
	}
	
	public void testGet() {
		try {
			PersistenceSession s = factory.openSession();
			Book b = (Book) s.get(Book.class, 1);
			System.out.println(b.getTitle());
			assertEquals(b.getTitle(), "Java");
		} catch (PersistenceException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testQuery() throws Exception {
		PersistenceSession s = factory.openSession();
		Criteria query = s.createCriteria(Book.class);
		@SuppressWarnings("unchecked")
		List<Book> l = (List<Book>) query.list();
		assertTrue(l.size() == 1);
		System.out.println(l.get(0).getTitle());
		s.close();
	}

	@Test
	public void testUpdate() throws Exception {
		PersistenceSession s = factory.openSession();
		Criteria query = s.createCriteria(Book.class);
		@SuppressWarnings("unchecked")
		List<Book> l = (List<Book>) query.list();
		Book b = l.get(0);
		b.setTitle("Un autre titre");
		s.update(b);
		Book bb = (Book) s.get(Book.class, b.getId());
		assertEquals(b.getId(), bb.getId());
		assertEquals(b.getTitle(), bb.getTitle());
		assertEquals(b.getPrice(), bb.getPrice());
		s.close();
	}

}
