package supercoderz.pushpak.server.mina;

import java.util.regex.Pattern;

import org.apache.mina.core.session.IoSession;

import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.server.IListener;
import supercoderz.pushpak.utils.Constants;
import supercoderz.pushpak.utils.LogUtils;

public class MinaListener implements IListener {

	IoSession session;
	String subjectRegex;
	Pattern pattern;

	public MinaListener(IoSession session, String subjectRegex) {
		this.session = session;
		this.subjectRegex = subjectRegex;
		pattern = Pattern.compile(subjectRegex);
		LogUtils.info("MinaListener", "Created listener for subject pattern "
				+ subjectRegex + " and channel context "
				+ session.getRemoteAddress().toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * supercoderz.pushpak.server.IListener#onMessage(supercoderz.pushpak.message
	 * .Message)
	 */
	public void onMessage(Message message) {
		String subject = (String) message.get(Constants.MESSAGE_SUBJECT);
		if (pattern.matcher(subject).matches()) {
			LogUtils.info("MinaListener",
					"Forwarding new message on subject pattern " + subjectRegex
							+ " and channel context "
							+ session.getRemoteAddress().toString());
			// write the message back to the requester using the context
			session.write(MessageFactory.createResponse(subjectRegex, message));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.server.IListener#getSubjectPattern()
	 */
	public String getSubjectPattern() {
		return subjectRegex;
	}

}
