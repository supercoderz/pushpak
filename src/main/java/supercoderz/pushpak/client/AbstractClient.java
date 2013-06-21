package supercoderz.pushpak.client;

import java.util.Map;

import supercoderz.pushpak.message.BulkMessageRetrieveRequest;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.message.PublishRequest;
import supercoderz.pushpak.message.Request;
import supercoderz.pushpak.utils.Constants;
import supercoderz.pushpak.utils.LogUtils;

public abstract class AbstractClient implements IClient {

	/**
	 * Send the message over the connection to the server
	 * 
	 * @param message
	 *            the message to be sent
	 */
	public abstract void send(Object message);

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.client.IClient#createEmptyMessage()
	 */
	public Message createEmptyMessage() {
		LogUtils.debug("AbstractClient", "Creating empty message for publish");
		return MessageFactory.createEmptyMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.client.IClient#createMessage(java.util.Map)
	 */
	public Message createMessage(Map<String, Object> data) {
		LogUtils.debug("AbstractClient",
				"Creating message with given data for publish");
		return MessageFactory.createMessage(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.client.IClient#publish(java.lang.String,
	 * supercoderz.pushpak.message.Message)
	 */
	public void publish(String subject, Message message) throws ClientException {
		LogUtils.debug("AbstractClient", "Publishing message on subject "
				+ subject);
		message.put(Constants.MESSAGE_SUBJECT, subject);
		PublishRequest pub = MessageFactory.createPublishRequest(message,
				subject);
		send(pub);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.client.IClient#subscribe(java.lang.String)
	 */
	public void subscribe(String subjectRegex) throws ClientException {
		LogUtils.debug("AbstractClient",
				"Subscribing message on subject pattern" + subjectRegex);
		Request request = MessageFactory.createRequest(subjectRegex);
		send(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.client.IClient#getMessages(java.lang.String)
	 */
	public void getMessages(String subjectRegex) throws ClientException {
		LogUtils.debug("AbstractClient",
				"Retrieving all messages on subject pattern" + subjectRegex);
		BulkMessageRetrieveRequest request = MessageFactory
				.createBulkMessageRetrieveRequest(subjectRegex);
		send(request);
	}

}
