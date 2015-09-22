package persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import persistence.configuration.PersistenceConfiguration;
import persistence.session.PersistenceSessionFactory;

public class PersistenceUtil {
  private static PersistenceConfiguration configuration;
  private static PersistenceSessionFactory factory;
  private static Logger log = LoggerFactory.getLogger(PersistenceSessionFactory.class);
  
  static {
    try {
      configuration = new PersistenceConfiguration("persistence/test/persistence-config-test.xml");
      factory = new PersistenceSessionFactory(configuration);
    } catch (Throwable ex) {
      log.error("Building PersistenceContextFactory failed.", ex);
      throw new ExceptionInInitializerError(ex);
    }
  }
  public static PersistenceConfiguration getConfiguration() {
    return configuration;
  }
  
  public static PersistenceSessionFactory getFactory() {
    return factory;
  }
}
