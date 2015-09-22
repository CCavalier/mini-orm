package persistence.test;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import persistence.PersistenceUtil;
import persistence.configuration.PersistenceConfiguration;
import persistence.configuration.PersistentClass;
import persistence.exception.PersistenceException;
import persistence.test.entity.Book;
import persistence.test.entity.Category;
import persistence.test.entity.Customer;
import persistence.test.entity.Order;

public class TestPersistentClass extends TestCase {
	
	private PersistenceConfiguration configuration;
	private PersistentClass bookPersistentClass;
	private PersistentClass categoryPersistentClass;
	private PersistentClass customerPersistentClass;
	private PersistentClass orderPersistentClass;

	@Before
	public void setUp() throws PersistenceException {
		configuration = PersistenceUtil.getConfiguration();
		bookPersistentClass = configuration.getPersistentClass(Book.class);
		categoryPersistentClass = configuration.getPersistentClass(Category.class);
		customerPersistentClass = configuration.getPersistentClass(Customer.class);
		orderPersistentClass = configuration.getPersistentClass(Order.class);
	}

	@Test
	public void testCreateInstance() {
		try {
			Object o = bookPersistentClass.createInstance();
			assertTrue(o instanceof Book);
			Object o1 = categoryPersistentClass.createInstance();
			assertTrue(o1 instanceof Category);
			Object o2 = customerPersistentClass.createInstance();
			assertTrue(o2 instanceof Customer);
			Object o3 = orderPersistentClass.createInstance();
			assertTrue(o3 instanceof Order);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetSelectQueryValue() {
		String bookSelectExpected = "book.id, book.title, book.price, book.fk_category";
		String categorySelectExpected = "category.id, category.title";
		String customerSelectExpected = "customer.name, customer.id";
		String orderSelectExpected = "order.num, order.quantity, customer.name, customer.id, book.id, book.title, book.price, book.fk_category";
		assertEquals(bookSelectExpected, bookPersistentClass.getSelectQueryValue());
		assertEquals(categorySelectExpected, categoryPersistentClass.getSelectQueryValue());
		assertEquals(customerSelectExpected, customerPersistentClass.getSelectQueryValue());
		assertEquals(orderSelectExpected, orderPersistentClass.getSelectQueryValue());
	}
	
	@Test
	public void testGetFromQueryValue() {
		String bookFromExpected = "book";
		String categoryFromExpected = "category";
		String customerFromExpected = "customer";
		String orderFromExpected = "order left outer join customer on order.fk_customer = customer.id left outer join book on order.fk_book = book.id";
		assertEquals(bookFromExpected, bookPersistentClass.getFromQueryValue());
		assertEquals(categoryFromExpected, categoryPersistentClass.getFromQueryValue());
		assertEquals(customerFromExpected, customerPersistentClass.getFromQueryValue());
		assertEquals(orderFromExpected, orderPersistentClass.getFromQueryValue());
	}
	
	public void testGetCreateTableStatement() {
		System.out.println("Generation query create table");
		String createBookTest = bookPersistentClass.getCreateTableStatement();
		System.out.println(createBookTest);
		String createCategoryTest = categoryPersistentClass.getCreateTableStatement();
		System.out.println(createCategoryTest);
		String createCustomerTest = customerPersistentClass.getCreateTableStatement();
		System.out.println(createCustomerTest);
		String createOrderTest = orderPersistentClass.getCreateTableStatement();
		System.out.println(createOrderTest);
		System.out.println();
		
		String createBookExpected = "CREATE TABLE book(id INT NOT NULL AUTO_INCREMENT,title VARCHAR(255),price DECIMAL(10,2),fk_category BIGINT, PRIMARY KEY (id), FOREIGN KEY (fk_category) REFERENCES category(id))";
		String createCategoryExpected = "CREATE TABLE category(id BIGINT NOT NULL AUTO_INCREMENT,title VARCHAR(255), PRIMARY KEY (id))";
		String createCustomerExpected = "CREATE TABLE customer(name VARCHAR(255),id NUMERIC NOT NULL, PRIMARY KEY (id))";
		String createOrderExpected = "CREATE TABLE order(num VARCHAR(255) NOT NULL,quantity INT,fk_customer NUMERIC,fk_book INT, PRIMARY KEY (num), FOREIGN KEY (fk_customer) REFERENCES customer(id), FOREIGN KEY (fk_book) REFERENCES book(id))";
		assertEquals(createBookExpected, createBookTest);
		assertEquals(createCategoryExpected, createCategoryTest);
		assertEquals(createCustomerExpected, createCustomerTest);
		assertEquals(createOrderExpected, createOrderTest);
	}
	
	public void testGetDropTableStatement() {
		System.out.println("Generation query drop table");
		String dropBookTest = bookPersistentClass.getDropTableStatement();
		System.out.println(dropBookTest);
		String dropCategoryTest = categoryPersistentClass.getDropTableStatement();
		System.out.println(dropCategoryTest);
		String dropCustomerTest = customerPersistentClass.getDropTableStatement();
		System.out.println(dropCustomerTest);
		String dropOrderTest = orderPersistentClass.getDropTableStatement();
		System.out.println(dropOrderTest);
		System.out.println();
		
		String dropBookExpected = "DROP TABLE book";
		String dropCategoryExpected = "DROP TABLE category";
		String dropCustomerExpected = "DROP TABLE customer";
		String dropOrderExpected = "DROP TABLE order";
		assertEquals(dropBookExpected, dropBookTest);
		assertEquals(dropCategoryExpected, dropCategoryTest);
		assertEquals(dropCustomerExpected, dropCustomerTest);
		assertEquals(dropOrderExpected, dropOrderTest);
	}
	
	public void testGetInsertStatement() {
		System.out.println("Generation query insert");
		String insertBookTest = bookPersistentClass.getInsertStatement();
		System.out.println(insertBookTest);
		String insertCategoryTest = categoryPersistentClass.getInsertStatement();
		System.out.println(insertCategoryTest);
		String insertCustomerTest = customerPersistentClass.getInsertStatement();
		System.out.println(insertCustomerTest);
		String insertOrderTest = orderPersistentClass.getInsertStatement();
		System.out.println(insertOrderTest);
		System.out.println();
		
		String insertBookExpected = "INSERT INTO book(title,price,fk_category) VALUES(?,?,?)";
		String insertCategoryExpected = "INSERT INTO category(title) VALUES(?)";
		String insertCustomerExpected = "INSERT INTO customer(name,id) VALUES(?,?)";
		String insertOrderExpected = "INSERT INTO order(num,quantity,fk_customer,fk_book) VALUES(?,?,?,?)";
		assertEquals(insertBookExpected, insertBookTest);
		assertEquals(insertCategoryExpected, insertCategoryTest);
		assertEquals(insertCustomerExpected, insertCustomerTest);
		assertEquals(insertOrderExpected, insertOrderTest);
	}
	
	public void testGetUpdateStatement() {
		System.out.println("Generation query update");
		String updateBookTest = bookPersistentClass.getUpdateStatement();
		System.out.println(updateBookTest);
		String updateCategoryTest = categoryPersistentClass.getUpdateStatement();
		System.out.println(updateCategoryTest);
		String updateCustomerTest = customerPersistentClass.getUpdateStatement();
		System.out.println(updateCustomerTest);
		String updateOrderTest = orderPersistentClass.getUpdateStatement();
		System.out.println(updateOrderTest);
		System.out.println();
		
		String updateBookExpected = "UPDATE book SET title= ?, price= ?, fk_category= ? WHERE id = ?";
		String updateCategoryExpected = "UPDATE category SET title= ?, null= ? WHERE id = ?";
		String updateCustomerExpected = "UPDATE customer SET name= ?, null= ? WHERE id = ?";
		String updateOrderExpected = "UPDATE order SET quantity= ?, fk_customer= ?, fk_book= ? WHERE num = ?";
		assertEquals(updateBookExpected, updateBookTest);
		assertEquals(updateCategoryExpected, updateCategoryTest);
		assertEquals(updateCustomerExpected, updateCustomerTest);
		assertEquals(updateOrderExpected, updateOrderTest);
	}
	
	public void testGetSelectByPropertyStatement() {
		System.out.println("Generation query select by property");
		String selectBookTest = bookPersistentClass.getSelectByPropertyStatement(bookPersistentClass.getPersistentProperty("title"));
		System.out.println(selectBookTest);
		String selectCategoryTest = categoryPersistentClass.getSelectByPropertyStatement(categoryPersistentClass.getPersistentProperty("title"));
		System.out.println(selectCategoryTest);
		String selectCustomerTest = customerPersistentClass.getSelectByPropertyStatement(customerPersistentClass.getPersistentProperty("name"));
		System.out.println(selectCustomerTest);
		String selectOrderTest = orderPersistentClass.getSelectByPropertyStatement(orderPersistentClass.getPersistentProperty("quantity"));
		System.out.println(selectOrderTest);
		System.out.println();
		
		String selectBookExpected = "SELECT book.id, book.title, book.price, book.fk_category FROM book WHERE book.title = ?";
		String selectCategoryExpected = "SELECT category.id, category.title FROM category WHERE category.title = ?";
		String selectCustomerExpected = "SELECT customer.name, customer.id FROM customer WHERE customer.name = ?";
		String selectOrderExpected = "SELECT order.num, order.quantity, customer.name, customer.id, book.id, book.title, book.price, book.fk_category FROM order left outer join customer on order.fk_customer = customer.id left outer join book on order.fk_book = book.id WHERE order.quantity = ?";
		assertEquals(selectBookExpected, selectBookTest);
		assertEquals(selectCategoryExpected, selectCategoryTest);
		assertEquals(selectCustomerExpected, selectCustomerTest);
		assertEquals(selectOrderExpected, selectOrderTest);
	}
	
	public void testGetSelectByIdStatement () {
		System.out.println("Generation query select by id");
		String selectBookTest = bookPersistentClass.getSelectByIdStatement();
		System.out.println(selectBookTest);
		String selectCategoryTest = categoryPersistentClass.getSelectByIdStatement();
		System.out.println(selectCategoryTest);
		String selectCustomerTest = customerPersistentClass.getSelectByIdStatement();
		System.out.println(selectCustomerTest);
		String selectOrderTest = orderPersistentClass.getSelectByIdStatement();
		System.out.println(selectOrderTest);
		System.out.println();
		
		String selectBookExpected = "SELECT book.id, book.title, book.price, book.fk_category FROM book WHERE book.id = ?";
		String selectCategoryExpected = "SELECT category.id, category.title FROM category WHERE category.id = ?";
		String selectCustomerExpected = "SELECT customer.name, customer.id FROM customer WHERE customer.id = ?";
		String selectOrderExpected = "SELECT order.num, order.quantity, customer.name, customer.id, book.id, book.title, book.price, book.fk_category FROM order left outer join customer on order.fk_customer = customer.id left outer join book on order.fk_book = book.id WHERE order.num = ?";
		assertEquals(selectBookExpected, selectBookTest);
		assertEquals(selectCategoryExpected, selectCategoryTest);
		assertEquals(selectCustomerExpected, selectCustomerTest);
		assertEquals(selectOrderExpected, selectOrderTest);
	}
	
	public void testGetSelectAllStatement () {
		System.out.println("Generation query select all");
		String selectBookTest = bookPersistentClass.getSelectAllStatement();
		System.out.println(selectBookTest);
		String selectCategoryTest = categoryPersistentClass.getSelectAllStatement();
		System.out.println(selectCategoryTest);
		String selectCustomerTest = customerPersistentClass.getSelectAllStatement();
		System.out.println(selectCustomerTest);
		String selectOrderTest = orderPersistentClass.getSelectAllStatement();
		System.out.println(selectOrderTest);
		System.out.println();
		
		String selectBookExpected = "SELECT book.id, book.title, book.price, book.fk_category FROM book";
		String selectCategoryExpected = "SELECT category.id, category.title FROM category";
		String selectCustomerExpected = "SELECT customer.name, customer.id FROM customer";
		String selectOrderExpected = "SELECT order.num, order.quantity, customer.name, customer.id, book.id, book.title, book.price, book.fk_category FROM order left outer join customer on order.fk_customer = customer.id left outer join book on order.fk_book = book.id";
		assertEquals(selectBookExpected, selectBookTest);
		assertEquals(selectCategoryExpected, selectCategoryTest);
		assertEquals(selectCustomerExpected, selectCustomerTest);
		assertEquals(selectOrderExpected, selectOrderTest);
	}
	
	
}
