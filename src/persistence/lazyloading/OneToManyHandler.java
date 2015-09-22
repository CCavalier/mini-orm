package persistence.lazyloading;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javassist.util.proxy.MethodHandler;
import persistence.configuration.OneToManyProperty;
import persistence.configuration.PersistentClass;
import persistence.session.PersistenceSession;
import persistence.session.ResultSetParser;

public class OneToManyHandler implements MethodHandler {
	
	private PersistenceSession session;
	private OneToManyProperty property;
	private Object id;
	private boolean isLoaded;
	
	public OneToManyHandler() {
		isLoaded = false;
	}

	public PersistenceSession getSession() {
		return session;
	}

	public void setSession(PersistenceSession session) {
		this.session = session;
	}

	public OneToManyProperty getProperty() {
		return property;
	}

	public void setProperty(OneToManyProperty property) {
		this.property = property;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object invoke(Object bean, Method method, Method proceed, Object[] args) throws Throwable {
		if (isLoaded == false) {
			isLoaded = true;
			PersistentClass targetClass = getProperty().getMappedBy().getOwner();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append(targetClass.getSelectQueryValue());
			sb.append(" FROM ");
			sb.append(targetClass.getFromQueryValue());
			sb.append(" WHERE ");
			sb.append(targetClass.getTableName()+"."+getProperty().getMappedBy().getColumnName());
			sb.append(" = ?");
			String query = sb.toString();
			PreparedStatement statement = getSession().getConnection().prepareStatement(query);
			statement.setObject(1, getId());
			ResultSet result = statement.executeQuery();
			ResultSetParser parser = new ResultSetParser(result, getProperty().getTargetClass(), getSession());
			while (parser.hasNext()) {
				if (bean instanceof List) {
					((List)bean).add(parser.getEntity());
				}
			}
		}
		return proceed.invoke(bean, args);
	}

}
