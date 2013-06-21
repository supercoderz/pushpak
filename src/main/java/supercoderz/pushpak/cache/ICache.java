package supercoderz.pushpak.cache;

import java.util.Iterator;

import supercoderz.pushpak.message.Message;

public interface ICache {

	/**
	 * Get the message stored in the cache on this subject
	 * @param subject the subject to check in the cache
	 * @return the message stored in the cache for this subject
	 */
	public Message get(String subject);

	/**
	 * Check if the cache contains any message on this subject. If the cache does not
	 * contain the message then it is likely that the message was never received or that 
	 * it was written to persistence layer
	 * @param subject the subject to check in the cache
	 * @return true if the message is in the cache
	 */
	public boolean contains(String subject);

	/**
	 * Insert a new message into the cache for the given subject
	 * @param subject the subject of the message
	 * @param message the message to be inserted to the cache
	 */
	public void put(String subject, Message message);
	
	/**
	 * Empty the cache
	 */
	public void flush();

	/**
	 * Return an iterator of all the messages in the cache
	 * 
	 * @return the iterator of all the messages in the cache
	 */
	public Iterator<Message> values();

}