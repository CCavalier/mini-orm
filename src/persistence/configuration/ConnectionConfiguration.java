/**
 * 
 */
package persistence.configuration;


/**
 * Contient les paramètres de la connexion à la base de donnée
 * @author dga
 *
 */
public class ConnectionConfiguration {
  private String driverClass;
	private String url;
	private String userName;
	private String password;
	private C3P0Configuration c3p0Configuration;

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
  
	public C3P0Configuration getC3p0Configuration() {
		return c3p0Configuration;
	}
	
	public void setC3p0Configuration(C3P0Configuration c3p0Configuration) {
		this.c3p0Configuration = c3p0Configuration;
	}
  
  

}
