package supercoderz.pushpak.message;

import java.io.Serializable;

public class BulkMessageRetrieveRequest implements Serializable {

	private static final long serialVersionUID = 2970488002512089477L;

	String subjectRegex;

	BulkMessageRetrieveRequest(String subjectRegex) {
		this.subjectRegex = subjectRegex;
	}

	public String getSubjectRegex() {
		return subjectRegex;
	}

}

