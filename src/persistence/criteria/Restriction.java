package persistence.criteria;

import persistence.configuration.AbstractPersistentProperty;

public class Restriction {

	private AbstractPersistentProperty property;
	private String comparator;
	private Object value;
	
	public Restriction(AbstractPersistentProperty property, String comparator, Object value) {
		this.property = property;
		this.comparator = comparator;
		this.value = value;
	}

	public AbstractPersistentProperty getProperty() {
		return property;
	}

	public void setProperty(AbstractPersistentProperty property) {
		this.property = property;
	}

	public String getComparator() {
		return comparator;
	}

	public void setComparator(String comparator) {
		this.comparator = comparator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
