package persistence.criteria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import persistence.configuration.AbstractPersistentProperty;
import persistence.configuration.ManyToOneProperty;
import persistence.configuration.OneToManyProperty;
import persistence.configuration.PersistentClass;
import persistence.configuration.SimpleColumnProperty;
import persistence.exception.CriteriaException;
import persistence.exception.PersistenceException;
import persistence.session.PersistenceSession;
import persistence.session.ResultSetParser;

public class Criteria {
	
	private PersistentClass persistentClass;
	private PersistenceSession session;
	private List<Restriction> restrictions;
	
	private static Logger log = LoggerFactory.getLogger(PersistenceSession.class);
	
	public Criteria() {
		
	}
	
	public List<Restriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<Restriction> restrictions) {
		this.restrictions = restrictions;
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

	public Criteria(PersistentClass aPersistentClass, PersistenceSession aSession) {
		persistentClass=aPersistentClass;
		session = aSession;
		restrictions = new LinkedList<Restriction>();
	}
	
	private void createRestriction(AbstractPersistentProperty property, String comparator, Object value) throws CriteriaException {
		if (property instanceof OneToManyProperty) {
			throw new CriteriaException("La propriete "+property.getName()+" est une propriété one to many. Les contraintes sur ce type de propriété ne sont pas implémentées");
		}
		if (property instanceof ManyToOneProperty) {
			ManyToOneProperty manyProperty = (ManyToOneProperty) property;
			if (manyProperty.isLazy() == true) {
				throw new CriteriaException("La propriété "+property.getName()+" est une propriété lazy. Le rajout de la jointure n'as pas encore été implémenté");
			} else {
				Restriction restriction = new Restriction(property, comparator, value);
				getRestrictions().add(restriction);
			}
		}
		if (property instanceof SimpleColumnProperty) {
			Restriction restriction = new Restriction(property, comparator, value);
			getRestrictions().add(restriction);
		}
		
	}
	public void addRestriction(String propertyName, String comparator, Object value) throws CriteriaException {
		AbstractPersistentProperty property = getPersistentClass().getPersistentProperty(propertyName);
		if (property == null) {
			throw new CriteriaException("La propriete "+propertyName+" n'éxiste pas pour la classe "+getPersistentClass().getClazz().getName());
		}
		createRestriction(property, comparator, value);
	}
	
	private String generateWhereRestrictions() {
		StringBuilder sb = new StringBuilder();
		for (Restriction lRestriction : getRestrictions()) {
			sb.append("AND ");
			sb.append(lRestriction.getProperty().getOwner().getTableName()+"."+lRestriction.getProperty().getColumnName());
			sb.append(" "+lRestriction.getComparator()+" ?");
		}
		return sb.delete(0, 4).toString();
	}
	
	public String generateQuery() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(getPersistentClass().getSelectQueryValue());
		sb.append(" FROM ");
		sb.append(getPersistentClass().getFromQueryValue());
		if (getRestrictions().size() > 0) {
			sb.append(" WHERE "+generateWhereRestrictions());
		}
		return sb.toString();
	}
	
	private void setParameters(PreparedStatement statement) throws SQLException {
		for (int i = 0; i < getRestrictions().size(); i++) {
			Restriction aRestriction = getRestrictions().get(i);
			statement.setObject(i + 1, (String) aRestriction.getValue());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List list() throws PersistenceException {
		try {
			Connection aConnection = getSession().getConnection();
			String query = generateQuery();
			PreparedStatement statement = aConnection.prepareStatement(query);
			setParameters(statement);
			log.info("Execution de la requete : "+ query);
			ResultSet resultSet = statement.executeQuery();
			ResultSetParser rsParser = new ResultSetParser(resultSet, getPersistentClass(), getSession());
			List<Object> results = new ArrayList<Object>();
			while (rsParser.hasNext()) {
				results.add(rsParser.getEntity());
			}
			return results;
		} catch (SQLException exception) {
			throw new PersistenceException(exception.getMessage());
		}
	}
	
	public Object singleResult() throws PersistenceException {
		try {
			Connection aConnection = getSession().getConnection();
			String query = generateQuery();
			PreparedStatement statement = aConnection.prepareStatement(query);
			setParameters(statement);
			log.info("Execution de la requete : "+ query);
			ResultSet resultSet = statement.executeQuery();
			ResultSetParser rsParser = new ResultSetParser(resultSet, getPersistentClass(), getSession());
			if (rsParser.hasNext() == true) {
				Object result = rsParser.getEntity();
				if (rsParser.hasNext() == true) {
					throw new PersistenceException("La requete a renvoye plusieurs resultats");
				}
				return result;
			} else {
				throw new PersistenceException("La requete n'a renvoye aucun resultat");
			}
		} catch (SQLException exception) {
			throw new PersistenceException(exception.getMessage());
		}
	}
	
}
