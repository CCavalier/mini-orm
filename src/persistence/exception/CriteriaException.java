package persistence.exception;

public class CriteriaException extends Exception {

	private static final long serialVersionUID = 4179131833288445454L;
	private static final String EXCEPTION_SUFFIX = "Une exception s'est produite lors de la generation de la criteria : ";
	
	public CriteriaException(String message) {
		super(EXCEPTION_SUFFIX+message);
	}
	
	public CriteriaException(String message, Throwable t) {
		super(EXCEPTION_SUFFIX+message, t);
	}

}
