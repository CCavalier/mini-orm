package persistence.configuration;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import persistence.annotation.ManyToOne;
import persistence.annotation.OneToMany;
import persistence.annotation.Transient;
import persistence.exception.PersistenceException;


public class PersistentClass {

	/*
	 * Le nom complet de la classe Java La classe Java Le nom de la table
	 * associée La liste des attributs persistants. La propriété qui sert de
	 * clé primaire
	 */

	private Class<?> clazz;
	private String tableName;
	private List<AbstractPersistentProperty> persistentProperties;
	private AbstractPersistentProperty id;
	private String name;

	public PersistentClass() {
		persistentProperties = new LinkedList<AbstractPersistentProperty>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<AbstractPersistentProperty> getPersistentProperties() {
		return persistentProperties;
	}

	public void setPersistentProperties(List<AbstractPersistentProperty> persistentProperties) {
		this.persistentProperties = persistentProperties;
	}

	public AbstractPersistentProperty getId() {
		return id;
	}

	public void setId(AbstractPersistentProperty id) {
		this.id = id;
	}

	public AbstractPersistentProperty getPersistentProperty(String string) {
		Iterator<AbstractPersistentProperty> i = persistentProperties.iterator();
		AbstractPersistentProperty temp;
		while (i.hasNext()) {
			temp = i.next();
			if (temp.getName().equals(string)) {
				return temp;
			}
		}
		return null;
	}

	public void parse() throws PersistenceException {
		try {
			clazz = Class.forName(getName());
		} catch (ClassNotFoundException e) {
			throw new PersistenceException("Mapped class not found: "
					+ getName());
		}
		setTableName(clazz.getSimpleName().toLowerCase()); // par defaut

		// Iterer sur les méthodes get pour déterminer les attributs
		// persistants
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			if (methodName.startsWith("get") // Une méthode get
					&& method.getParameterTypes().length == 0 // Sans paramètres
					&& method.getDeclaringClass().equals(clazz) // Non héritée
					&& method.getAnnotation(Transient.class) == null) { // Et non transient
				
				if (method.getAnnotation(OneToMany.class) == null
					&& method.getAnnotation(ManyToOne.class) == null) {
					AbstractPersistentProperty property = new SimpleColumnProperty(this, method);
					addPersistentProperty(property);
				}
			}
		}
	}
	
	public void parseForeignKey(PersistenceConfiguration aConfiguration) throws PersistenceException {
		try {
			clazz = Class.forName(getName());
		} catch (ClassNotFoundException e) {
			throw new PersistenceException("Mapped class not found: "
					+ getName());
		}
		setTableName(clazz.getSimpleName().toLowerCase());
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			if (methodName.startsWith("get") // Une méthode get
					&& method.getParameterTypes().length == 0 // Sans paramètres
					&& method.getDeclaringClass().equals(clazz) // Non héritée
					&& method.getAnnotation(Transient.class) == null) { // Et non transient

				if (method.getAnnotation(ManyToOne.class) != null) {
					PersistentClass aReturnClass = aConfiguration.getPersistentClass(method.getReturnType());
					if (aReturnClass == null) {
						throw new PersistenceException("Mapped class not found: "+ method.getReturnType().getName());
					}
					AbstractPersistentProperty property = new ManyToOneProperty(this, method, aReturnClass);
					addPersistentProperty(property);
				}
				
				if (method.isAnnotationPresent(OneToMany.class)) {
					String propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
					Class<?> rclazz = method.getReturnType();
					if (!rclazz.isAssignableFrom(List.class)) {
						throw new PersistenceException("La propriété "+propertyName+" n'est pas une List");
					}
					Type type;
					try {
						type = getClazz().getDeclaredField(propertyName).getGenericType();
						ParameterizedType pt = (ParameterizedType) type;  
						if (pt.getActualTypeArguments().length > 0) {  
							Class<?> aReturnGeneric = (Class<?>)pt.getActualTypeArguments()[0];
							PersistentClass aReturnClass = aConfiguration.getPersistentClass(aReturnGeneric);
							if (aReturnClass == null) {
								throw new PersistenceException("Mapped class not found: "+ aReturnGeneric.getName());
							}
							AbstractPersistentProperty property = new OneToManyProperty(this, method, aReturnClass);
							addPersistentProperty(property);
						}  
					} catch (Exception e) {
						throw new PersistenceException(e.getMessage());
					}
				}
			}
		}
	}

	private void addPersistentProperty(AbstractPersistentProperty property) {
		this.persistentProperties.add(property);
	}

	public String getCreateTableStatement() {
		StringBuffer sb = new StringBuffer();
		StringBuffer constraints = new StringBuffer();
		sb.append("CREATE TABLE ");
		sb.append(this.tableName);
		sb.append("(");
		for (AbstractPersistentProperty pp : persistentProperties) {
			if (pp instanceof OneToManyProperty == false) {
				sb.append(pp.getColumnName());
				sb.append(" ");
				sb.append(pp.getSqlType());
				sb.append(",");
				if (pp instanceof SimpleColumnProperty && ((SimpleColumnProperty)pp).isId()) {
					constraints.append(", PRIMARY KEY ("+ pp.getColumnName() +")");
				}
				if (pp instanceof ManyToOneProperty) {
					ManyToOneProperty manyToOneProperty = (ManyToOneProperty) pp;
					constraints.append(", FOREIGN KEY ("+ pp.getColumnName() +") REFERENCES "+ manyToOneProperty.getTargetClass().getTableName() +"("+ manyToOneProperty.getTargetClass().getId().getColumnName() +")");
				}
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(constraints);
		sb.append(")");
		return sb.toString();
	}

	public String getDropTableStatement() {
		StringBuffer sb = new StringBuffer();
		sb.append("DROP TABLE ");
		sb.append(this.tableName);
		return sb.toString();
	}

	public String getInsertStatement() {
		// (un prepared statement avec des ? pour les valeurs colonnes)
		// insert into book(title,price,category_id) values (?,?,?)
		StringBuffer sb = new StringBuffer();
		StringBuffer values = new StringBuffer();
		sb.append("INSERT INTO ");
		sb.append(this.tableName);
		sb.append("(");
		values.append("(");
		for (AbstractPersistentProperty pp : persistentProperties) {
			if (pp instanceof OneToManyProperty == false) {
				if (pp instanceof SimpleColumnProperty) {
					SimpleColumnProperty column = (SimpleColumnProperty) pp;
					if (column.isAutoGenerated()) {
						continue;
					}
				}
				sb.append(pp.getColumnName());
				sb.append(",");
				values.append("?,");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") VALUES");
		values.deleteCharAt(values.length() - 1);
		values.append(")");
		sb.append(values);
		return sb.toString();
	}

	public String getUpdateStatement() {
		// (avec des ? pour les valeurs des colonnes et un ? pour l'Id
		// update book set title=?,price=?,category_id=? where id = ?
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE ");
		sb.append(this.tableName);
		sb.append(" SET ");
		for (AbstractPersistentProperty pp : persistentProperties) {
			if (pp instanceof SimpleColumnProperty) {
				if (((SimpleColumnProperty)pp).isId()) {
					continue;
				}
			}
			sb.append(pp.getColumnName());
			sb.append("= ?, ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("WHERE "+ getId().getColumnName() +" = ?");
		return sb.toString();
	}

	public String getSelectByIdStatement() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(getSelectQueryValue());
		sb.append(" FROM ");
		sb.append(getFromQueryValue());
		sb.append(" WHERE ");
		sb.append(getTableName()+"."+getId().getColumnName());
		sb.append(" = ?");
		return sb.toString();
	}

	public String getSelectByPropertyStatement(AbstractPersistentProperty pp) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(getSelectQueryValue());
		sb.append(" FROM ");
		sb.append(getFromQueryValue());
		sb.append(" WHERE ");
		sb.append(pp.getOwner().getTableName()+"."+pp.getColumnName());
		sb.append(" = ?");
		return sb.toString();
	}

	public String getSelectAllStatement() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(getSelectQueryValue());
		sb.append(" FROM ");
		sb.append(getFromQueryValue());
		return sb.toString();
	}
	
	public String getSelectQueryValue() {
		StringBuilder result = new StringBuilder();
		for (AbstractPersistentProperty property : getPersistentProperties()) {
			if (property instanceof SimpleColumnProperty) {
				result.append(", "+getTableName()+"."+property.getColumnName());
			}
			if (property instanceof ManyToOneProperty) {
				ManyToOneProperty manyToOne = (ManyToOneProperty) property;
				if (manyToOne.isLazy()) {
					result.append(", "+property.getOwner().getTableName()+"."+property.getColumnName());
				} else {
					result.append(", "+manyToOne.getTargetClass().getSelectQueryValue());
				}
			}
			if (property instanceof OneToManyProperty) {
				// TODO
			}
		}
		return result.delete(0, 2).toString();
	}
	
	public String getFromQueryValue() {
		StringBuilder result = new StringBuilder();
		result.append(", "+getTableName());
		for (AbstractPersistentProperty property : getPersistentProperties()) {
			if (property instanceof ManyToOneProperty) {
				ManyToOneProperty manyToOne = (ManyToOneProperty) property;
				if (manyToOne.isLazy() == false) {
					result.append(" left outer join "+manyToOne.getTargetClass().getFromQueryValue());
					result.append(" on ");
					result.append(getTableName()+"."+property.getColumnName());
					result.append(" = ");
					result.append(manyToOne.getTargetClass().getTableName()+"."+manyToOne.getTargetClass().getId().getColumnName());
				}
			}
			if (property instanceof OneToManyProperty) {
				// TODO
			}
		}
		return result.delete(0, 2).toString();
	}

	public Object createInstance() throws PersistenceException {
		Object created = null;
		try {
			created = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new PersistenceException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new PersistenceException(e.getMessage());
		}
		return created;
	}

}
