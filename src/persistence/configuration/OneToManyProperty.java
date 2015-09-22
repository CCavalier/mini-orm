package persistence.configuration;

import java.lang.reflect.Method;

import persistence.annotation.OneToMany;
import persistence.exception.PersistenceException;

public class OneToManyProperty extends AbstractPersistentProperty {

	private PersistentClass targetClass;
	private AbstractPersistentProperty mappedBy;
	private boolean lazy;

	public PersistentClass getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(PersistentClass targetClass) {
		this.targetClass = targetClass;
	}

	public AbstractPersistentProperty getMappedBy() {
		return mappedBy;
	}

	public void setMappedBy(AbstractPersistentProperty mappedBy) {
		this.mappedBy = mappedBy;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}
	
	public OneToManyProperty(PersistentClass aPersistentClass, Method aMethod, PersistentClass aTargetClass) throws PersistenceException {
		setMethod(aMethod);
		setOwner(aPersistentClass);
		setTargetClass(aTargetClass);
		parse(aPersistentClass);
	}

	@Override
	public void parse(PersistentClass aPersistentClass) throws PersistenceException {
		String methodName = getMethod().getName();
		String propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
		setName(propertyName);
		Class<?> rclazz = getMethod().getReturnType();
		setClazz(rclazz);
		OneToMany annotation = getMethod().getAnnotation(OneToMany.class);
		if (!annotation.mappedBy().equals("")) {
			setMappedBy(getTargetClass().getPersistentProperty(annotation.mappedBy()));
		} else {
			setMappedBy(getTargetClass().getPersistentProperty(getName()));
		}
		setLazy(annotation.lazy());
	}

}
