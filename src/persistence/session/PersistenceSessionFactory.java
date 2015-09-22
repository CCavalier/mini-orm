package persistence.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import persistence.configuration.PersistenceConfiguration;
import persistence.exception.PersistenceException;

public class PersistenceSessionFactory {

	protected static ThreadLocal<PersistenceSession> currentSession = new ThreadLocal<PersistenceSession>();
	private PersistenceConfiguration persistenceConfiguration;
	
	private static Logger log = LoggerFactory.getLogger(PersistenceSessionFactory.class);

	public PersistenceSessionFactory(
			PersistenceConfiguration aPersistenceConfiguration) {
		persistenceConfiguration = aPersistenceConfiguration;
	}

	public PersistenceConfiguration getPersistenceConfiguration() {
		return persistenceConfiguration;
	}

	public void setPersistenceConfiguration(PersistenceConfiguration connectionConfiguration) {
		this.persistenceConfiguration = connectionConfiguration;
	}

	public PersistenceSession openSession() throws PersistenceException {
		PersistenceSession session = new PersistenceSession(this);
		currentSession.set(session);
		return session;
	}

	public PersistenceSession geCurrentSession() throws PersistenceException {
		log.debug("Getting current Persistence Session ");
		PersistenceSession session = currentSession.get();
		if (session == null) {
			session = openSession();
			currentSession.set(session);
		}
		return session;
	}
	
	public void close(PersistenceSession session) throws PersistenceException {
	    log.debug("Closing Persistence Session ");
	    session.close();
	    PersistenceSession current = currentSession.get();
	    if (current != null && current == session) {
	      currentSession.set(null);
	    }
	  }

}
