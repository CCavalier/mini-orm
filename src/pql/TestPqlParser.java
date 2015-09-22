package pql;

import org.junit.Test;


import junit.framework.TestCase;

public class TestPqlParser extends TestCase {

	private static final String MISSING_SELECT_QUERY = " * from Entity";
	private static final String MISSING_FROM_QUERY = "Select * Entity";
	
	@Test
	public void testParsePql() throws Exception {
		try {
			PqlParser.parsePql(MISSING_SELECT_QUERY);
			fail("Parsing select fail");
		} catch (QueryException exception ) {
			
		}
		try {
			PqlParser.parsePql(MISSING_FROM_QUERY);
			fail("Parsing from fail");
		} catch (QueryException exception) {
			
		}
	}
}
