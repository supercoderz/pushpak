package supercoderz.pushpak.persistence;

import java.util.List;

import supercoderz.pushpak.message.Message;

public interface IPersistenceManager {
	/**
	 * Save a message to the underlying persistence layer.
	 * @param message the message to be saved
	 * @throws PersistenceException optionally, throws exception when the persist operation fails
	 */
	public void persist(Message message) throws PersistenceException;
	
	
	/**
	 * Get the message for the given subject from the underlying persistence mechanism
	 * @param subject the subject on which the message was originally published
	 * @return the message extracted from the persistence layer
	 * @throws PersistenceException optionally throws exception when there are errors with retrieving the message
	 */
	public Message getMessage(String subject) throws PersistenceException;
	
	/**
	 * Get all the messages that satisfy the given subject pattern
	 * 
	 * @param subjectRegex
	 *            the subject regex pattern
	 * @return the list of all messages for that subject pattern
	 * @throws PersistenceException
	 *             optionally throws an exception if there is an error in
	 *             reading the messages
	 */
	public List<Message> getMessages(String subjectRegex)
			throws PersistenceException;

	/**
	 * Shutdown the persistence process
	 */
	public void shutdown();
	
	

}
