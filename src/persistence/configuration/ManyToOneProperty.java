package persistence.configuration;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

import persistence.annotation.ManyToOne;
import persistence.exception.PersistenceException;


public class ManyToOneProperty extends AbstractPersistentProperty {
	
	private static final String FK_COLUMN_PREFIX = "fk_";

	private PersistentClass targetClass;
	private boolean lazy = false;
	
	public ManyToOneProperty(PersistentClass aPersistentClass, Method aMethod, PersistentClass aTargetClass) throws PersistenceException {
		setOwner(aPersistentClass);
		setMethod(aMethod);
		setTargetClass(aTargetClass);
		parse(aPersistentClass);
	}

	public PersistentClass getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(PersistentClass targetClass) {
		this.targetClass = targetClass;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	@Override
	public void parse(PersistentClass aPersistentClass) throws PersistenceException {
		String methodName = getMethod().getName();
		String propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
		setName(propertyName);
		Class<?> rclazz = getMethod().getReturnType();
		setClazz(rclazz);
		ManyToOne colum2 = getMethod().getAnnotation(ManyToOne.class);
		if (colum2 != null) {
			setLazy(colum2.lazy());
			setColumnName(FK_COLUMN_PREFIX + getTargetClass().getTableName());
			String type = getTargetClass().getId().getSqlType();
			setSqlType(type.split(" ")[0]);
		}
	}
	
	public Object getIdValue(Object bean) throws PersistenceException {
		try {
			Object targetInstance = PropertyUtils.getSimpleProperty(bean, getName());
			return getTargetClass().getId().getValue(targetInstance);
		} catch (Exception e) {
			throw new PersistenceException("Cannot get property " + getName()
					+ " on object of class " + bean.getClass());
		}
	}
	
	
	
	@Override
	public void setValue(Object bean, Object value) throws PersistenceException {
		if (getClazz().isInstance(value)) {
			super.setValue(bean, value);
		} else {
			try {
				Object valueConverted = ConvertUtils.convert(value, getTargetClass().getId().getClazz());
				Object newInstance = getClazz().newInstance();
				getTargetClass().getId().setValue(newInstance, valueConverted);
			} catch (Exception e) {
				throw new PersistenceException("Cannot set foreign key property " + getTargetClass().getId().getName()
						+ " on object of class " + getTargetClass().getClass());
			}
		}
	}

}
