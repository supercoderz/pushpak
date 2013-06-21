package supercoderz.pushpak.server;

import supercoderz.pushpak.message.Message;

public interface IListener {

	/**
	 * Notify the listener that a new message has been received
	 * 
	 * @param message
	 *            the message that was received
	 */
	public void onMessage(Message message);

	/**
	 * Return the regex pattern that will match the subjects accepted by this
	 * listener
	 * 
	 * @return the regex pattern
	 */
	public String getSubjectPattern();
}
