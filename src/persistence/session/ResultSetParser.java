package persistence.session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javassist.util.proxy.ProxyFactory;
import persistence.configuration.AbstractPersistentProperty;
import persistence.configuration.ManyToOneProperty;
import persistence.configuration.OneToManyProperty;
import persistence.configuration.PersistentClass;
import persistence.configuration.SimpleColumnProperty;
import persistence.exception.PersistenceException;
import persistence.lazyloading.ManyToOneFilter;
import persistence.lazyloading.ManyToOneHandler;
import persistence.lazyloading.OneToManyFilter;
import persistence.lazyloading.OneToManyHandler;

public final class ResultSetParser {
	
	private ResultSet resultSet;
	private PersistentClass persistentClass;
	private PersistenceSession session;
	
	public ResultSetParser(ResultSet aResultSet, PersistentClass aPersistentClass, PersistenceSession aSession) {
		resultSet = aResultSet;
		persistentClass = aPersistentClass;
		session = aSession;
	}
	
	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public PersistentClass getPersistentClass() {
		return persistentClass;
	}

	public void setPersistentClass(PersistentClass persistentClass) {
		this.persistentClass = persistentClass;
	}

	public PersistenceSession getSession() {
		return session;
	}

	public void setSession(PersistenceSession session) {
		this.session = session;
	}
	
	private String getReturnColumnNameForProperty(AbstractPersistentProperty property) {
		return property.getOwner().getTableName()+"."+property.getColumnName();
	}

	private void setManyToOnePropertyForBean(Object bean, ManyToOneProperty property) throws SQLException, PersistenceException {
		if (property.isLazy()) {
			try {
				ProxyFactory factory = new ProxyFactory();
				factory.setSuperclass(property.getClazz());
				factory.setFilter(new ManyToOneFilter());
				ManyToOneHandler handler = new ManyToOneHandler();
				handler.setSession(getSession());
				Object lId = getResultSet().getObject(property.getOwner().getTableName()+"."+property.getColumnName());
				handler.setId(lId);
				handler.setPersistentClass(property.getTargetClass());
				factory.setHandler(handler);
				Class<?> proxyClass = factory.createClass();
				Object newProxy = proxyClass.newInstance();
				property.getTargetClass().getId().setValue(newProxy, lId);
				property.setValue(bean, newProxy);
			} catch (Exception e) {
				throw new PersistenceException(e.getMessage());
			}
		} else {
			ResultSetParser nestedParser = new ResultSetParser(getResultSet(), property.getTargetClass(), getSession());
			Object nestedEntity = nestedParser.getEntity();
			property.setValue(bean, nestedEntity);
		}
	}
	
	private void setSimpleColumnPropertyForBean(Object bean, SimpleColumnProperty property) throws SQLException, PersistenceException {
		Object columnValue = getResultSet().getObject(getReturnColumnNameForProperty(property));
		property.setValue(bean, columnValue);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setOneToManyPropertyForBean(Object bean, OneToManyProperty property) throws SQLException, PersistenceException {
		if (property.isLazy()) {
			try {
				ProxyFactory factory = new ProxyFactory();
				factory.setSuperclass(ArrayList.class);
				factory.setFilter(new OneToManyFilter());
				OneToManyHandler handler = new OneToManyHandler();
				handler.setSession(getSession());
				Object lId = getResultSet().getObject(getReturnColumnNameForProperty(getPersistentClass().getId()));
				handler.setId(lId);
				handler.setProperty(property);
				factory.setHandler(handler);
				Class<?> proxyClass = factory.createClass();
				Object newProxy = proxyClass.newInstance();
				property.setValue(bean, newProxy);
			} catch (Exception e) {
				throw new PersistenceException(e.getMessage());
			}
		} else {
			Object foreignKeyId = getResultSet().getObject(getReturnColumnNameForProperty(getPersistentClass().getId()));
			PersistentClass targetClass = property.getMappedBy().getOwner();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append(targetClass.getSelectQueryValue());
			sb.append(" FROM ");
			sb.append(targetClass.getFromQueryValue());
			sb.append(" WHERE ");
			sb.append(targetClass.getTableName()+"."+property.getMappedBy().getColumnName());
			sb.append(" = ?");
			String query = sb.toString();
			PreparedStatement statement = getSession().getConnection().prepareStatement(query);
			statement.setObject(1, foreignKeyId);
			ResultSet result = statement.executeQuery();
			ResultSetParser parser = new ResultSetParser(result, property.getTargetClass(), getSession());
			List collection = new ArrayList();
			while (parser.hasNext()) {
				collection.add(parser.getEntity());
			}
			property.setValue(bean, collection);
		}
	}
	
	public Object nextEntity() throws PersistenceException {
		Object result = getPersistentClass().createInstance();
		
		return result;
	}

	public boolean hasNext() throws SQLException {
		return getResultSet().next();
	}

	public Object getEntity() throws SQLException, PersistenceException {
		Object result = getPersistentClass().createInstance();
		for (AbstractPersistentProperty property : getPersistentClass().getPersistentProperties()) {
			if (property instanceof SimpleColumnProperty) {
				setSimpleColumnPropertyForBean(result, (SimpleColumnProperty) property);
			}
			if (property instanceof ManyToOneProperty) {
				setManyToOnePropertyForBean(result, (ManyToOneProperty) property);
			}
			if (property instanceof OneToManyProperty) {
				setOneToManyPropertyForBean(result, (OneToManyProperty) property);
			}
		}
		return result;
	}
	
	

}
