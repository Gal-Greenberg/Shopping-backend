package collab.logic;

public class ActionInvocationException extends RuntimeException {
	private static final long serialVersionUID = -1302219513603259807L;

	public ActionInvocationException() {
		super();
	}

	public ActionInvocationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionInvocationException(String message) {
		super(message);
	}

	public ActionInvocationException(Throwable cause) {
		super(cause);
	}

}