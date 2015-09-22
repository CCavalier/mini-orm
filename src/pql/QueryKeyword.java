package pql;

public enum QueryKeyword {
	
	SELECT("SELECT"),
	FROM("FROM"),
	WHERE("WHERE"),
	GROUP_BY("GROUP BY"),
	HAVING("HAVING"),
	ORDER_BY("ORDER BY"),
	AND("AND");
	
	private String sqlSyntax;
	
	public String getSqlSyntax() {
		return sqlSyntax;
	}
	
	private QueryKeyword(String aSql) {
		sqlSyntax = aSql;
	}

}
