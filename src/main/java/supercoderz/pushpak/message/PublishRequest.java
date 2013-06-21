package supercoderz.pushpak.message;

import java.io.Serializable;

public class PublishRequest implements Serializable{
	
	private static final long serialVersionUID = -1541491019312532554L;

	Message message;
	String subject;

	PublishRequest(Message message, String subject) {
		this.message = message;
		this.subject = subject;
	}

	public Message getMessage() {
		return message;
	}

	public String getSubject() {
		return subject;
	}

}
