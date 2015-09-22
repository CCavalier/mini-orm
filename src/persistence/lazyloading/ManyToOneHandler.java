package persistence.lazyloading;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import persistence.configuration.AbstractPersistentProperty;
import persistence.configuration.PersistentClass;
import persistence.session.PersistenceSession;

public class ManyToOneHandler implements MethodHandler {
	
	private PersistenceSession session;
	private PersistentClass persistentClass;
	private Object id;
	private boolean isLoaded;
	
	public ManyToOneHandler() {
		isLoaded = false;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public PersistenceSession getSession() {
		return session;
	}

	public void setSession(PersistenceSession session) {
		this.session = session;
	}

	public PersistentClass getPersistentClass() {
		return persistentClass;
	}

	public void setPersistentClass(PersistentClass persistentClass) {
		this.persistentClass = persistentClass;
	}

	@Override
	public Object invoke(Object bean, Method method, Method proceed, Object[] args) throws Throwable {
		if (isLoaded == false) {
			Object loadedInstance = getSession().get(getPersistentClass().getClazz(), getId());
			for (AbstractPersistentProperty lProperty : getPersistentClass().getPersistentProperties()) {
				lProperty.setValue(bean, lProperty.getValue(loadedInstance));
			}
			isLoaded = true;
		}
		return proceed.invoke(bean, args);
	}

}
