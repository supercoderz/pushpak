package supercoderz.pushpak.client;

import java.util.List;

import supercoderz.pushpak.message.Message;

public interface IClientMessageHandler {

	/**
	 * Receive the message from the server and handle accordingly. This needs to
	 * be implemented if you are subscribing messages for a subject regex from
	 * the server
	 * 
	 * @param message
	 *            the message received from the server
	 * @throws ClientException
	 *             throws an exception in case of errors
	 */
	public void onMessage(Message message) throws ClientException;

	/**
	 * Receive a list of messages from the server and handle them accordingly.
	 * This needs to be implemented if you are retrieving all messages for a
	 * subject regex from the server.
	 * 
	 * @param messages
	 * @throws ClientException
	 */
	public void handleMessages(List<Message> messages) throws ClientException;

}
