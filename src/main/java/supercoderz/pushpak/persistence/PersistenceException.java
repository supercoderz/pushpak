package supercoderz.pushpak.persistence;

public class PersistenceException extends Exception {

	private static final long serialVersionUID = -8791918059002418683L;

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(Throwable cause) {
		super(cause);
	}

}
