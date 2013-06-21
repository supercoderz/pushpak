package supercoderz.pushpak.message;

import java.util.List;
import java.util.Map;

public class MessageFactory {
	
	/**
	 * Create a empty Message
	 * @return the map message
	 */
	public static Message createEmptyMessage(){
		return new Message();
	}
	
	/**
	 * Create a Message based on the given data map
	 * @param data the map to be used for creating the message
	 * @return the message created with the data
	 */
	public static Message createMessage(Map<String,Object> data){
		return new Message(data);
	}

	/**
	 * Create a response object with the given subject pattern and message
	 * 
	 * @param subjectRegex
	 * @param message
	 * @return
	 */
	public static Response createResponse(String subjectRegex, Message message) {
		return new Response(subjectRegex, message);
	}

	/**
	 * Create a request object for a given subject regex. This will be used to
	 * create a subscription for the subject.
	 * 
	 * @param subjectRegex
	 * @return
	 */
	public static Request createRequest(String subjectRegex) {
		return new Request(subjectRegex);
	}

	/**
	 * Create a publish request that can be used to publish a message to the
	 * server
	 * 
	 * @param message
	 *            the message to be published
	 * @param subject
	 *            the message subject
	 * @return the publish request for this message
	 */
	public static PublishRequest createPublishRequest(Message message,
			String subject) {
		return new PublishRequest(message, subject);
	}

	/**
	 * Create a retrieve request that can be used to fetch the message for the
	 * given subject from the server.
	 * 
	 * @param subject
	 *            the subject of the message
	 * @return the retrieve request for this subject
	 */
	public static RetrieveRequest createRetrieveRequest(String subject) {
		return new RetrieveRequest(subject);
	}

	/**
	 * Create a retrieve request that can be used to fetch all the message for
	 * the given subject regex from the server.
	 * 
	 * @param subjectRegex
	 *            the regex of the message subject
	 * @return bulk retrieve request for this pattern
	 */
	public static BulkMessageRetrieveRequest createBulkMessageRetrieveRequest(
			String subjectRegex) {
		return new BulkMessageRetrieveRequest(subjectRegex);
	}

	/**
	 * Create a response object to return all messages matching a subject regex
	 * 
	 * @param subjectRegex
	 *            the subject regex to match all the messages
	 * @param messages
	 *            the list of messages for this regex
	 * @return bulk retrieve response for this regex
	 */
	public static BulkMessageRetrieveResponse createBulkMessageRetrieveResponse(
			String subjectRegex, List<Message> messages) {
		return new BulkMessageRetrieveResponse(subjectRegex, messages);
	}

}
