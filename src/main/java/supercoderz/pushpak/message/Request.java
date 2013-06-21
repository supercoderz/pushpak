package supercoderz.pushpak.message;

import java.io.Serializable;

public class Request implements Serializable{
	
	private static final long serialVersionUID = 2278976900999255378L;

	String subjectRegex;

	Request(String subjectRegex) {
		this.subjectRegex = subjectRegex;
	}

	public String getSubectRegex() {
		return subjectRegex;
	}

}
