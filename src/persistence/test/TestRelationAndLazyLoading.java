package persistence.test;

import java.math.BigDecimal;
import java.util.List;

import javassist.util.proxy.ProxyFactory;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import persistence.PersistenceUtil;
import persistence.configuration.AbstractPersistentProperty;
import persistence.configuration.ManyToOneProperty;
import persistence.configuration.OneToManyProperty;
import persistence.configuration.PersistenceConfiguration;
import persistence.configuration.PersistentClass;
import persistence.criteria.Criteria;
import persistence.exception.CriteriaException;
import persistence.exception.PersistenceException;
import persistence.lazyloading.ManyToOneFilter;
import persistence.lazyloading.ManyToOneHandler;
import persistence.session.PersistenceSession;
import persistence.session.PersistenceSessionFactory;
import persistence.test.entity.Book;
import persistence.test.entity.Category;
import persistence.test.entity.Customer;
import persistence.test.entity.Order;

public class TestRelationAndLazyLoading extends TestCase {
	
	private PersistenceConfiguration configuration;
	private PersistenceSessionFactory factory;

	@Before
	public void setUp() throws PersistenceException {
		configuration = PersistenceUtil.getConfiguration();
		factory = new PersistenceSessionFactory(configuration);
	}
	
	@Test
	public void testTechnicalLinksOneToMany() {
		PersistentClass pc = configuration.getPersistentClass(Category.class);
		AbstractPersistentProperty pp = pc.getPersistentProperty("books");
		assertTrue(pp instanceof OneToManyProperty);
		OneToManyProperty oneProperty = (OneToManyProperty) pp;
		assertTrue(oneProperty.getTargetClass().getClazz().equals(Book.class));
		assertTrue(oneProperty.getClazz().isAssignableFrom(List.class));
	}
	
	@Test
	public void testTechnicalLinksManyToOne() {
		PersistentClass pc = configuration.getPersistentClass(Book.class);
		AbstractPersistentProperty pp = pc.getPersistentProperty("category");
		assertTrue(pp instanceof ManyToOneProperty);
		ManyToOneProperty manyProperty = (ManyToOneProperty) pp;
		assertTrue(manyProperty.getTargetClass().getClazz().equals(Category.class));
		assertTrue(Category.class.isAssignableFrom(manyProperty.getClazz()));
	}
	
	@Test
	public void testProxyCreation() throws Exception {
		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(Category.class);
		factory.setFilter(new ManyToOneFilter());
		ManyToOneHandler handler = new ManyToOneHandler();
		factory.setHandler(handler);
		Class<?> proxyClass = factory.createClass();
		Object newProxy = proxyClass.newInstance();
		assertTrue(proxyClass.isInstance(newProxy));
		assertTrue(Category.class.isAssignableFrom(proxyClass.newInstance().getClass()));
	}
	
	@Test
	public void testManyToOneLazy() throws PersistenceException, CriteriaException {
		PersistenceSession session = factory.openSession();
		Criteria aCriteria = session.createCriteria(Book.class);
		aCriteria.addRestriction("title", "=", new String("Les deux tours"));
		Book aResult = (Book) aCriteria.singleResult();
		assertNotSame(aResult.getCategory().getClass(), Category.class);
		assertNotNull(aResult.getId());
		assertEquals("Fantasy", aResult.getCategory().getTitle());
	}
	
	@Test
	public void testManyToOneEager() throws PersistenceException {
		PersistenceSession session = factory.openSession();
		Order aResult = (Order) session.get(Order.class, "00000000001");
		assertEquals(aResult.getBook().getClass(), Book.class);
		assertEquals("La communauté de l'anneau", aResult.getBook().getTitle());
		assertEquals(aResult.getCustomer().getClass(), Customer.class);
		assertEquals("Pierre Martin", aResult.getCustomer().getName());
	}
	
	@Test
	public void testOneToManyLazy() throws PersistenceException, CriteriaException {
		PersistenceSession session = factory.openSession();
		Criteria aCriteria = session.createCriteria(Category.class);
		aCriteria.addRestriction("title", "=", new String("Fantasy"));
		Category aResult = (Category) aCriteria.singleResult();
		assertEquals(3, aResult.getBooks().size());
	}
	
	@Test
	public void testOneToManyEager() throws PersistenceException {
		PersistenceSession session = factory.openSession();
		Customer aResult = (Customer) session.get(Customer.class, new BigDecimal(0));
		assertEquals(1, aResult.getCommandes().size());
	}


}
