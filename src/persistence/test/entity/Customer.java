package persistence.test.entity;

import java.math.BigDecimal;
import java.util.List;

import persistence.annotation.Id;
import persistence.annotation.OneToMany;

public class Customer {
	
	private BigDecimal id;
	private String name;
	private List<Order> commandes;
	
	public Customer() {
		
	}

	@Id
	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(lazy=false)
	public List<Order> getCommandes() {
		return commandes;
	}

	public void setCommandes(List<Order> commandes) {
		this.commandes = commandes;
	}

}
