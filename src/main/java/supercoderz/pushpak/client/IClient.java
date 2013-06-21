package supercoderz.pushpak.client;

import java.util.Map;

import supercoderz.pushpak.message.Message;


public interface IClient {

	/**
	 * Connect to the server running on the given host and port.
	 * 
	 * @param hostname
	 *            the host where the server is running
	 * @param port
	 *            the port on which the server is listening
	 * @param handler
	 *            the message handler that will be invoked when a message is
	 *            received
	 * @throws ClientException
	 *             throws exception in case there are errors in making
	 *             connection
	 */
	public void connect(String hostname, int port, IClientMessageHandler handler)
			throws ClientException;

	/**
	 * Disconnect from the server
	 * 
	 * @throws ClientException
	 *             throws exception if there are errors in disconnecting
	 */
	public void disconnect() throws ClientException;

	/**
	 * Create an empty message that can be published to the server
	 * 
	 * @return the new message
	 */
	public Message createEmptyMessage();

	/**
	 * Create a message and fill it with the data from the given map
	 * 
	 * @param data
	 *            the data to be filled in the message
	 * @return the message containing the given data
	 */
	public Message createMessage(Map<String, Object> data);

	/**
	 * Publish a message on the given subject to the server
	 * 
	 * @param subject
	 *            the subject of the message
	 * @param message
	 *            the message to be published
	 * @throws ClientException
	 *             throws exception in case of errors during publish
	 */
	public void publish(String subject, Message message) throws ClientException;

	/**
	 * Subscribe to all messages that satisfy the given regex pattern. The
	 * handler will be invoked each time a message is received.
	 * 
	 * @param subjectRegex
	 *            the regex pattern of the subject
	 * @throws ClientException
	 */
	public void subscribe(String subjectRegex)
			throws ClientException;

	/**
	 * Get all the messages that satisfy the given regex - this is a bulk
	 * retrieve operation and there is no subscription started.
	 * 
	 * @param subjectRegex
	 *            the regex pattern of the subject
	 * @throws ClientException
	 */
	public void getMessages(String subjectRegex)
			throws ClientException;
}
