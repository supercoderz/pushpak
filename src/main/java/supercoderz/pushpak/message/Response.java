package supercoderz.pushpak.message;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = 5067562213571642041L;

	String subjectRegex;
	Message message;

	Response(String subjectRegex, Message message) {
		this.subjectRegex = subjectRegex;
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	public String getSubjectRegex() {
		return subjectRegex;
	}

}
