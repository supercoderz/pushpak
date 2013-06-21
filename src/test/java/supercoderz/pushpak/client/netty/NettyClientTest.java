package supercoderz.pushpak.client.netty;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import supercoderz.pushpak.client.ClientException;
import supercoderz.pushpak.client.IClientMessageHandler;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.server.netty.PushpakNettyServer;

public class NettyClientTest {

	PushpakNettyServer server = new PushpakNettyServer(1111, "data", 10);
	Message received;
	List<Message> receivedMessages;

	@Before
	public void setup() {
		server.startServer();
	}

	@After
	public void teardown() throws InterruptedException {
		server.stopServer();
		Thread.sleep(5000);
		for (File file : (new File("data")).listFiles()) {
			file.delete();
		}
	}

	@Test
	public void testPublishAndSubscribeMessage() throws ClientException,
			InterruptedException {
		NettyClient client = new NettyClient();
		IClientMessageHandler handler=new ClientHandler();
		client.connect("localhost", 1111, handler);
		client.subscribe("test.*");
		Message message = client.createEmptyMessage();
		message.put("KEY", "VALUE");
		client.publish("test.message.sent.1", message);
		Thread.sleep(1000);
		assertNotNull(received);
		assertTrue(received.get("KEY").equals("VALUE"));
		client.disconnect();
	}

	@Test
	public void testPublishAndSubscribeMessage1() throws ClientException,
			InterruptedException {
		NettyClient client = new NettyClient();
		IClientMessageHandler handler = new ClientHandler();
		client.connect("localhost", 1111, handler);
		client.subscribe("test.*");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("KEY", "VALUE");
		Message message = client.createMessage(data);
		client.publish("test.message.sent.1", message);
		Thread.sleep(1000);
		assertNotNull(received);
		assertTrue(received.get("KEY").equals("VALUE"));
		client.disconnect();
	}

	@Test
	public void testPublishAndRetrieveMessages() throws ClientException,
			InterruptedException {
		NettyClient client = new NettyClient();
		IClientMessageHandler handler = new ClientHandler();
		client.connect("localhost", 1111, handler);
		Message message = client.createEmptyMessage();
		message.put("KEY", "VALUE");
		client.publish("test.message.sent.1", message);
		client.publish("test.message.sent.2", message);
		client.publish("test.message.sent.3", message);
		Thread.sleep(1000);
		client.getMessages("test.*");
		Thread.sleep(1000);
		assertNotNull(receivedMessages);
		assertTrue(receivedMessages.size() == 3);
		client.disconnect();
	}

	@Test(expected = ClientException.class)
	public void testConnectInvalid() throws ClientException,
			InterruptedException {
		NettyClient client = new NettyClient();
		client.connect("localhost", 1111, null);
	}

	class ClientHandler implements IClientMessageHandler {

		public void onMessage(Message message) throws ClientException {
			received = message;
		}

		public void handleMessages(List<Message> messages)
				throws ClientException {
			receivedMessages = messages;
		}

	}

}
