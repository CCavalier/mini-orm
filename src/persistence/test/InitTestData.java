package persistence.test;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import persistence.configuration.PersistenceConfiguration;
import persistence.exception.PersistenceException;
import persistence.session.PersistenceSession;
import persistence.session.PersistenceSessionFactory;
import persistence.test.entity.Book;
import persistence.test.entity.Category;
import persistence.test.entity.Customer;
import persistence.test.entity.Order;

public class InitTestData extends TestCase {

	private PersistenceConfiguration configuration;
	private PersistenceSessionFactory factory;

	@Before
	public  void setUp() throws PersistenceException {
		configuration = new PersistenceConfiguration("persistence/test/persistence-config-test.xml");
		factory = new PersistenceSessionFactory(configuration);
	}
	
	@Test
	public void testInitData() {
		try {
			PersistenceSession s = factory.openSession();
			Category c = new Category();
			c.setTitle("Fantasy");
			s.save(c);
			Book book1 = new Book();
			book1.setPrice(new BigDecimal(8.0));
			book1.setTitle("La communauté de l'anneau");
			book1.setCategory(c);
			s.save(book1);
			Book book2 = new Book();
			book2.setPrice(new BigDecimal(12.0));
			book2.setTitle("Les deux tours");
			book2.setCategory(c);
			s.save(book2);
			Book book3 = new Book();
			book3.setPrice(new BigDecimal(12.5));
			book3.setTitle("Le retour du roi");
			book3.setCategory(c);
			s.save(book3);
			
			Customer customer1 = new Customer();
			customer1.setId(new BigDecimal(0));
			customer1.setName("Pierre Martin");
			s.save(customer1);
			
			Order order1 = new Order();
			order1.setNum("00000000001");
			order1.setBook(book1);
			order1.setCustomer(customer1);
			order1.setQuantity(1);
			s.save(order1);
			
			Order order2 = new Order();
			order2.setBook(book2);
			order2.setCustomer(customer1);
			order2.setQuantity(1);
			s.save(order2);
		} catch (PersistenceException e) {
			fail(e.getMessage());
		}
	}

}
