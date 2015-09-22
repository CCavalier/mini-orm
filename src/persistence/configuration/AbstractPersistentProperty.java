package persistence.configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

import persistence.exception.PersistenceException;

public abstract class AbstractPersistentProperty {
	
	@SuppressWarnings("serial")
	private static Map<Class<?>, String> java2sql = new HashMap<Class<?>, String>() {
		{
			put(String.class, "VARCHAR(255)");
			put(BigDecimal.class, "NUMERIC");
			put(Long.class, "BIGINT");
			put(Integer.class, "INT");
		}
	};
	@SuppressWarnings("serial")
	private static final Map<Type, Class<?>> primitiveWrapper = new HashMap<Type, Class<?>>() {
		{
			put(int.class, Integer.class);
			put(double.class, Double.class);
			put(long.class, Long.class);
			put(boolean.class, Boolean.class);
			put(float.class, Float.class);
			put(char.class, Character.class);
			put(byte.class, Byte.class);
			put(void.class, Void.class);
			put(short.class, Short.class);
		}
	};
	
	public static String getSqlTypeForClass(Class<?> aClass) {
		return java2sql.get(aClass);
	}
	
	private Method method;
	private String name;
	private Class<?> clazz;
	private String columnName;
	private String sqlType;
	private PersistentClass owner;
	
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
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

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
	
	public PersistentClass getOwner() {
		return owner;
	}

	public void setOwner(PersistentClass owner) {
		this.owner = owner;
	}

	public Class<?> getClassOrWrapper() {
		if (getClazz().isPrimitive()) {
			return primitiveWrapper.get(getClazz());
		}
		return getClazz();
	}

	public abstract void parse(PersistentClass aPersistentClass) throws PersistenceException;
	
	public Object getValue(Object bean) throws PersistenceException {
		try {
			return PropertyUtils.getSimpleProperty(bean, getName());
		} catch (Exception e) {
			throw new PersistenceException("Cannot get property " + getName()
					+ " on object of class " + bean.getClass());
		}

	}

	public void setValue(Object bean, Object value) throws PersistenceException {
		Object valueConverted = ConvertUtils.convert(value, getClazz());
		try {
			PropertyUtils.setSimpleProperty(bean, getName(), valueConverted);
		} catch (Exception e) {
			throw new PersistenceException("Cannot set property " + getName()
					+ " on object of class " + bean.getClass());
		}
	}

}
