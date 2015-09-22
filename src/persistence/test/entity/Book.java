/**
 * 
 */
package persistence.test.entity;

import java.math.BigDecimal;

import persistence.annotation.Column;
import persistence.annotation.Id;
import persistence.annotation.ManyToOne;

/**
 * @author dga
 * 
 */
public class Book {
	private String title;
	private int id;
	private BigDecimal price;

	private Category category;

	@Id(autoGenerated = true)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "price", type = "DECIMAL(10,2)")
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@ManyToOne(lazy = true)
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}
