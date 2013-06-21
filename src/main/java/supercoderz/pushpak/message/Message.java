package supercoderz.pushpak.message;

import java.util.LinkedHashMap;
import java.util.Map;

public class Message extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 5452845379313868544L;

	Message() {
	}

	Message(Map<? extends String, ? extends Object> m) {
		super(m);
	}

}
