package supercoderz.pushpak.server;

import java.util.List;

import supercoderz.pushpak.message.Message;

public interface IServerHandler {

	/**
	 * Accept a message from the network and process it further so that it can
	 * be retrieved later
	 * 
	 * @param message
	 *            the message received from the network
	 * @throws ServerException
	 *             throws an exception if there are any issues with processing
	 *             the message
	 */
	public void accept(Message message) throws ServerException;

	/**
	 * Retrieve a message that was previously received on this subject
	 * 
	 * @param subject
	 *            the subject of the message
	 * @return the message retrieved for this subject
	 * @throws ServerException
	 *             throws an exception if there were any errors in retrieving
	 *             the message
	 */
	public Message retrieve(String subject) throws ServerException;

	/**
	 * Retrieve all the messages for the given regex pattern
	 * 
	 * @param subjectRegex
	 *            the subject regex pattern
	 * @return the list of all messages on subjects that match this pattern
	 * @throws ServerException
	 *             throws exception in case of any issues
	 */
	public List<Message> retrieveMessages(String subjectRegex)
			throws ServerException;

	/**
	 * Shutdown the server.
	 * 
	 * @throws ServerException
	 *             throws exception if there are any errors in shutting down the
	 *             server
	 */
	public void shutdown() throws ServerException;

	/**
	 * Register a listener for the messages received on this handler
	 * 
	 * @param listener
	 *            the listener which will process the messages
	 */
	public void registerListener(IListener listener);

}
