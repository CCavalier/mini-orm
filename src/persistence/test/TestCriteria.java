package persistence.test;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import persistence.PersistenceUtil;
import persistence.configuration.PersistenceConfiguration;
import persistence.configuration.PersistentClass;
import persistence.criteria.Criteria;
import persistence.exception.CriteriaException;
import persistence.exception.PersistenceException;
import persistence.session.PersistenceSession;
import persistence.session.PersistenceSessionFactory;
import persistence.test.entity.Book;

public class TestCriteria extends TestCase {

	private PersistenceConfiguration configuration;
	private PersistenceSessionFactory factory;

	@Before
	public  void setUp() throws PersistenceException {
		configuration = PersistenceUtil.getConfiguration();
		factory = PersistenceUtil.getFactory();
	}

	@Test
	public void testCreateCriteria() throws PersistenceException {
		PersistentClass pcb = configuration.getPersistentClass(Book.class);
		PersistenceSession session = factory.openSession();
		Criteria aCriteria = new Criteria(pcb, session);
		System.out.println(aCriteria.generateQuery());
		assertNotNull(aCriteria);
	}

	@Test
	public void testAddRestriction() throws PersistenceException, CriteriaException {
		PersistentClass pcb = configuration.getPersistentClass(Book.class);
		PersistenceSession session = factory.openSession();
		Criteria aCriteria = new Criteria(pcb, session);
		aCriteria.addRestriction("title", "=", new String("Les deux tours"));
		System.out.println(aCriteria.generateQuery());
		assertEquals(aCriteria.getRestrictions().size(), 1);
	}

	@Test
	public void testMultipleAddRestriction() throws PersistenceException, CriteriaException {
		PersistentClass pcb = configuration.getPersistentClass(Book.class);
		PersistenceSession session = factory.openSession();
		Criteria aCriteria = new Criteria(pcb, session);
		aCriteria.addRestriction("title", "LIKE", "Les %");
		aCriteria.addRestriction("id", "=", 0);
		System.out.println(aCriteria.generateQuery());
		assertEquals(aCriteria.getRestrictions().size(), 2);
	}

	@Test
	public void testSingleResult() throws PersistenceException, CriteriaException {
		PersistentClass pcb = configuration.getPersistentClass(Book.class);
		PersistenceSession session = factory.openSession();
		Criteria aCriteria = new Criteria(pcb, session);
		aCriteria.addRestriction("title", "=", new String("Les deux tours"));
		System.out.println(aCriteria.generateQuery());
		Book aResult = (Book) aCriteria.singleResult();
		assertNotNull(aResult);
	}

	@Test
	public void testList() throws PersistenceException, CriteriaException {
		PersistentClass pcb = configuration.getPersistentClass(Book.class);
		PersistenceSession session = factory.openSession();
		Criteria aCriteria = new Criteria(pcb, session);
		aCriteria.addRestriction("title", "LIKE", new String("Le%"));
		System.out.println(aCriteria.generateQuery());
		@SuppressWarnings("unchecked")
		List<Book> result = aCriteria.list();
		assertNotNull(result);
		assertEquals(result.size(), 2);
	}

}
