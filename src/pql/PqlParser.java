package pql;

import java.util.HashMap;
import java.util.Map;

public class PqlParser {
	
	private static Map<QueryKeyword, String>  sqlStructureParsing(String upperPql) throws QueryException {
		if (!upperPql.contains(QueryKeyword.SELECT.getSqlSyntax())) {
			throw new QueryException("Il manque le mot clef select � la requete");
		}
		if (!upperPql.contains(QueryKeyword.FROM.getSqlSyntax())) {
			throw new QueryException("Il manque le mot clef from � la requete");
		}
		return new HashMap<QueryKeyword, String>();
	}
	
	public static String parsePql(String pql) throws QueryException {
		String upperPql = pql.toUpperCase();
		sqlStructureParsing(upperPql);
		return upperPql;
	}

}
