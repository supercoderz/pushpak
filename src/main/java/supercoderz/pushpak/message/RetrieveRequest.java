package supercoderz.pushpak.message;

import java.io.Serializable;

public class RetrieveRequest implements Serializable{
	
	private static final long serialVersionUID = -6323064264578150923L;

	String subject;

	RetrieveRequest(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

}
