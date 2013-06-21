package supercoderz.pushpak.server.mina;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import supercoderz.pushpak.message.BulkMessageRetrieveRequest;
import supercoderz.pushpak.message.BulkMessageRetrieveResponse;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.message.PublishRequest;
import supercoderz.pushpak.message.Request;
import supercoderz.pushpak.message.Response;
import supercoderz.pushpak.utils.Constants;

public class PushpakMinaServerTest {

	Map<String, Message> receivedMessages = new HashMap<String, Message>();
	Map<String, Integer> countMessages = new HashMap<String, Integer>();
	PushpakMinaServer server;

	IoSession ioSession;

	@Before
	public void setup() throws IOException {
		server = new PushpakMinaServer(1111, "./data", 10);
		server.startServer();
	}

	@After
	public void teardown() throws InterruptedException {
		server.stopServer();
		Thread.sleep(5000);
		File data = new File("./data");
		for (File file : data.listFiles()) {
			file.delete();
		}
		ioSession.close(true);
	}

	@Test
	public void testSendSubscribeMessageFromServer()
			throws InterruptedException, IOException {

		SocketConnector connector = new NioSocketConnector();
		connector.setHandler(new TestHandler());
		connector.getSessionConfig().setSendBufferSize(2048);
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		connector.connect(new InetSocketAddress("localhost", 1111)).await();

		Request request = MessageFactory.createRequest("test.*");
		ioSession.write(request).await();

		Message message = MessageFactory.createEmptyMessage();
		message.put(Constants.MESSAGE_SUBJECT, "test.message");
		for (int i = 0; i < 100; i++) {
			message.put(String.valueOf(i), i);
		}

		PublishRequest publish = MessageFactory.createPublishRequest(message,
				"test.message");

		ioSession.write(publish).await();

		// now wait
		Thread.sleep(500);

		assertTrue(receivedMessages.containsKey("test.message"));

		message = MessageFactory.createEmptyMessage();
		message.put(Constants.MESSAGE_SUBJECT, "test.message1");
		for (int i = 0; i < 100; i++) {
			message.put(String.valueOf(i), i);
		}

		publish = MessageFactory.createPublishRequest(message, "test.message1");

		ioSession.write(publish).await();

		// now wait
		Thread.sleep(500);

		assertTrue(receivedMessages.containsKey("test.message1"));

		(new File("./data/test.message.bson")).delete();
		(new File("./data/test.message1.bson")).delete();
		ioSession.close(false);

	}

	@Test
	public void testSendSubscribeMessageFromServerMultiple()
			throws InterruptedException, IOException {

		SocketConnector connector = new NioSocketConnector();
		connector.setHandler(new CountingTestHandler());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		connector.connect(new InetSocketAddress("localhost", 1111)).await();


		Request request = MessageFactory.createRequest("test.*");
		ioSession.write(request).await();

		request = MessageFactory.createRequest("test.message.*");
		ioSession.write(request).await();

		Message message = MessageFactory.createEmptyMessage();
		for (int i = 0; i < 100; i++) {
			message.put(String.valueOf(i), i);
		}

		for (int i = 0; i < 20; i++) {
			countMessages.put("test." + i, 0);
			countMessages.put("test.message." + i, 0);
		}

		for (int i = 0; i < 20; i++) {
			String subject = "test." + i;
			message.put(Constants.MESSAGE_SUBJECT, subject);
			PublishRequest publish = MessageFactory.createPublishRequest(
					message, subject);
			ioSession.write(publish);
		}

		for (int i = 0; i < 20; i++) {
			String subject = "test.message." + i;
			message.put(Constants.MESSAGE_SUBJECT, subject);
			PublishRequest publish = MessageFactory.createPublishRequest(
					message, subject);
			ioSession.write(publish);
		}

		// now wait
		Thread.sleep(3000);

		assertTrue(countMessages.size() == 40);

		for (int i = 0; i < 20; i++) {
			assertTrue(countMessages.get("test." + i) == 1);
			assertTrue(countMessages.get("test.message." + i) == 2);
		}

		for (int i = 0; i < 20; i++) {
			(new File("./data/test." + i + ".bson")).delete();
			(new File("./data/test.message." + i + ".bson")).delete();
		}
		ioSession.close(false);
	}

	@Test
	public void testSendSubscribeRetrieveMultiple()
			throws InterruptedException, IOException {

		SocketConnector connector = new NioSocketConnector();
		connector.setHandler(new MultipleMessagesTestHandler());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		connector.connect(new InetSocketAddress("localhost", 1111)).await();


		Message message = MessageFactory.createEmptyMessage();
		for (int i = 0; i < 100; i++) {
			message.put(String.valueOf(i), i);
		}

		for (int i = 0; i < 20; i++) {
			String subject = "test." + i;
			message.put(Constants.MESSAGE_SUBJECT, subject);
			PublishRequest publish = MessageFactory.createPublishRequest(
					message, subject);
			ioSession.write(publish);
		}

		// now wait
		Thread.sleep(3000);

		BulkMessageRetrieveRequest request = MessageFactory
				.createBulkMessageRetrieveRequest("test.*");
		ioSession.write(request);

		Thread.sleep(10000);

		assertTrue(receivedMessages.size() == 20);

		for (int i = 0; i < 20; i++) {
			(new File("./data/test." + i + ".bson")).delete();
		}
		ioSession.close(false);
	}

	class TestHandler extends IoHandlerAdapter {

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			ioSession = session;
			super.sessionCreated(session);
		}

		@Override
		public void messageReceived(IoSession ctx, Object message) {
			Response response = (Response) message;
			receivedMessages.put(
					(String) response.getMessage().get(
							Constants.MESSAGE_SUBJECT),
					response.getMessage());
		}
	}

	class CountingTestHandler extends IoHandlerAdapter {

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			ioSession = session;
			super.sessionCreated(session);
		}

		@Override
		public void messageReceived(IoSession ctx, Object message) {
			Response response = (Response) message;
			int count = countMessages.get((String) response.getMessage().get(
					Constants.MESSAGE_SUBJECT));
			countMessages.put(
					(String) response.getMessage().get(
							Constants.MESSAGE_SUBJECT), count + 1);
		}
	}

	class MultipleMessagesTestHandler extends IoHandlerAdapter {

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			ioSession = session;
			super.sessionCreated(session);
		}

		@Override
		public void messageReceived(IoSession ctx, Object message) {
			BulkMessageRetrieveResponse response = (BulkMessageRetrieveResponse) message;
			for (Message msg : response.getMessages()) {
				receivedMessages.put(
						(String) msg.get(Constants.MESSAGE_SUBJECT), msg);
			}
		}
	}

}
