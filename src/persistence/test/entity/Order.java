package persistence.test.entity;

import persistence.annotation.Id;
import persistence.annotation.ManyToOne;

public class Order {
	
	private String num;
	private Customer customer;
	private Book book;
	private Integer quantity;
	
	public Order() {
		
	}

	@Id
	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	@ManyToOne(lazy=false)
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@ManyToOne(lazy=false)
	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
