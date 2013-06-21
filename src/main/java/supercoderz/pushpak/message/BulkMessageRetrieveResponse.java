package supercoderz.pushpak.message;

import java.io.Serializable;
import java.util.List;

public class BulkMessageRetrieveResponse implements Serializable {

	private static final long serialVersionUID = 2970488002512089477L;

	String subjectRegex;
	List<Message> messages;

	BulkMessageRetrieveResponse(String subjectRegex, List<Message> messages) {
		this.subjectRegex = subjectRegex;
		this.messages = messages;
	}

	public String getSubjectRegex() {
		return subjectRegex;
	}

	public List<Message> getMessages() {
		return messages;
	}

}

