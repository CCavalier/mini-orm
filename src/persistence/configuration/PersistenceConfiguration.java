package persistence.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import persistence.exception.PersistenceException;

public class PersistenceConfiguration {
	private ConnectionConfiguration connectionConfiguration ;
	private List<PersistentClass> persistentClasses = new ArrayList<PersistentClass>();

	public ConnectionConfiguration getConnectionConfiguration() {
		return connectionConfiguration;
	}

	public void setConnectionConfiguration(
			ConnectionConfiguration connectionConfiguration) {
		this.connectionConfiguration = connectionConfiguration;
	}

	public List<PersistentClass> getPersistentClasses() {
		return persistentClasses;
	}

	public void setPersistentClasses(List<PersistentClass> persistentClasses) {
		this.persistentClasses = persistentClasses;
	}

	public PersistenceConfiguration(String string) throws PersistenceException {
		parseConfigFile(string);
		for(PersistentClass pc : persistentClasses){
			pc.parse();
		}
		for(PersistentClass pc : persistentClasses){
			pc.parseForeignKey(this);
		}
	}

	 private void parseConfigFile(String fileName) throws PersistenceException {
		    Digester digester = new Digester();
		    digester.setValidating(false);
		    digester.push(this);
		    digester.addObjectCreate("persistence-configuration/connection", ConnectionConfiguration.class);
		    digester.addBeanPropertySetter("persistence-configuration/connection/driver-class", "driverClass");
		    digester.addBeanPropertySetter("persistence-configuration/connection/url", "url");
		    digester.addBeanPropertySetter("persistence-configuration/connection/username", "userName");
		    digester.addBeanPropertySetter("persistence-configuration/connection/password", "password");
		    
		    digester.addObjectCreate("persistence-configuration/connection/c3p0", C3P0Configuration.class);
		    digester.addBeanPropertySetter("persistence-configuration/connection/c3p0/min-size", "minSize");
		    digester.addBeanPropertySetter("persistence-configuration/connection/c3p0/max-size", "maxSize");
		    digester.addBeanPropertySetter("persistence-configuration/connection/c3p0/timeout", "timeout");
		    digester.addBeanPropertySetter("persistence-configuration/connection/c3p0/max-statements", "maxStatements");
		    digester.addSetNext("persistence-configuration/connection/c3p0", "setC3p0Configuration");
		    
		    digester.addSetNext("persistence-configuration/connection", "addConnectionConfiguration");

		    digester.addObjectCreate("persistence-configuration/mapped-classes/mapped-class", PersistentClass.class);
		    digester.addSetProperties("persistence-configuration/mapped-classes/mapped-class", "name", "name");
		    digester.addSetNext("persistence-configuration/mapped-classes/mapped-class", "addPersistentClass");

		    InputStream s = getClass().getClassLoader().getResourceAsStream(fileName);
		    if (s == null) {
		      throw new PersistenceException("Persistence configuration file not found ");
		    }
		    try {
		      digester.parse(s);
		    } catch (IOException e) {
		      throw new PersistenceException("Exception while parsing config file :" + e.getMessage());
		    } catch (SAXException e) {
		      throw new PersistenceException("Exception while parsing config file :" + e.getMessage());
		    }
		  }

	public PersistentClass getPersistentClass(Class<?> oClass) {
		Iterator<PersistentClass> i= persistentClasses.iterator();
		while(i.hasNext())
		{
			PersistentClass temp= i.next();
			if(temp.getClazz() == oClass)
			{
				return temp;
			}
			
		}
		return null;
	}
	
	public void addConnectionConfiguration(ConnectionConfiguration cc){
		connectionConfiguration=cc;
	}
	
	public void addPersistentClass( PersistentClass pc){
		persistentClasses.add(pc);
	}
}
