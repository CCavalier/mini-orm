package persistence.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTest extends TestCase {
	
	public static Test suite() {
		 TestSuite suite = new TestSuite();
		 suite.addTestSuite(TestConfiguration.class);
		 suite.addTestSuite(TestPersistentClass.class);
		 suite.addTestSuite(TestSession.class);
		 suite.addTestSuite(InitTestData.class);
		 suite.addTestSuite(TestCriteria.class);
		 suite.addTestSuite(TestRelationAndLazyLoading.class);
		 return suite;
	}

}
