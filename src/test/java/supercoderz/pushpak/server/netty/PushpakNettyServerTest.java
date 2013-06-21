package supercoderz.pushpak.server.netty;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
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

public class PushpakNettyServerTest {

	Map<String, Message> receivedMessages = new HashMap<String, Message>();
	Map<String, Integer> countMessages = new HashMap<String, Integer>();
	PushpakNettyServer server;
	ChannelFuture channelFuture;

	@Before
	public void setup() {
		server = new PushpakNettyServer(1111, "./data", 10);
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
	}

	@Test
	public void testSendSubscribeMessageFromServer()
			throws InterruptedException {
		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new ObjectEncoder(),
						new ObjectDecoder(), new TestHandler());
			}
		});

		// Start the connection attempt.
		channelFuture = bootstrap.connect(new InetSocketAddress("localhost",
				1111));

		// wait for connection
		channelFuture.await();

		Request request = MessageFactory.createRequest("test.*");
		channelFuture.getChannel().write(request).await();

		Message message = MessageFactory.createEmptyMessage();
		message.put(Constants.MESSAGE_SUBJECT, "test.message");
		for (int i = 0; i < 100; i++) {
			message.put(String.valueOf(i), i);
		}

		PublishRequest publish = MessageFactory.createPublishRequest(message,
				"test.message");

		channelFuture.getChannel().write(publish);

		// now wait
		Thread.sleep(500);

		assertTrue(receivedMessages.containsKey("test.message"));

		message = MessageFactory.createEmptyMessage();
		message.put(Constants.MESSAGE_SUBJECT, "test.message1");
		for (int i = 0; i < 100; i++) {
			message.put(String.valueOf(i), i);
		}

		publish = MessageFactory.createPublishRequest(message, "test.message1");

		channelFuture.getChannel().write(publish);

		// now wait
		Thread.sleep(500);

		assertTrue(receivedMessages.containsKey("test.message1"));

		(new File("./data/test.message.bson")).delete();
		(new File("./data/test.message1.bson")).delete();

		channelFuture.getChannel().close();

	}

	@Test
	public void testSendSubscribeRetrieveMultiple()
			throws InterruptedException, IOException {

		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new ObjectEncoder(),
						new ObjectDecoder(), new MultipleMessagesTestHandler());
			}
		});

		// Start the connection attempt.
		channelFuture = bootstrap.connect(new InetSocketAddress("localhost",
				1111));

		// wait for connection
		channelFuture.await();

		Message message = MessageFactory.createEmptyMessage();
		for (int i = 0; i < 100; i++) {
			message.put(String.valueOf(i), i);
		}

		for (int i = 0; i < 20; i++) {
			String subject = "test." + i;
			message.put(Constants.MESSAGE_SUBJECT, subject);
			PublishRequest publish = MessageFactory.createPublishRequest(
					message, subject);
			channelFuture.getChannel().write(publish);
		}

		// now wait
		Thread.sleep(3000);

		BulkMessageRetrieveRequest request = MessageFactory
				.createBulkMessageRetrieveRequest("test.*");
		channelFuture.getChannel().write(request);

		Thread.sleep(5000);

		assertTrue(receivedMessages.size() == 20);

		for (int i = 0; i < 20; i++) {
			(new File("./data/test." + i + ".bson")).delete();
		}
		channelFuture.getChannel().close();
	}

	@Test
	public void testSendSubscribeMessageFromServerMultiple()
			throws InterruptedException {
		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new ObjectEncoder(),
						new ObjectDecoder(), new CountingTestHandler());
			}
		});

		// Start the connection attempt.
		channelFuture = bootstrap.connect(new InetSocketAddress("localhost",
				1111));

		// wait for connection
		channelFuture.await();

		Request request = MessageFactory.createRequest("test.*");
		channelFuture.getChannel().write(request).await();

		request = MessageFactory.createRequest("test.message.*");
		channelFuture.getChannel().write(request).await();

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
			channelFuture.getChannel().write(publish);
		}

		for (int i = 0; i < 20; i++) {
			String subject = "test.message." + i;
			message.put(Constants.MESSAGE_SUBJECT, subject);
			PublishRequest publish = MessageFactory.createPublishRequest(
					message, subject);
			channelFuture.getChannel().write(publish);
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

		channelFuture.getChannel().close();

	}

	class TestHandler extends SimpleChannelUpstreamHandler {
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
			Response response = (Response)e.getMessage();
			receivedMessages.put(
					(String) response.getMessage().get(
							Constants.MESSAGE_SUBJECT),
					response.getMessage());
		}
	}

	class CountingTestHandler extends SimpleChannelUpstreamHandler {
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			Response response = (Response) e.getMessage();
			int count = countMessages.get((String) response.getMessage().get(
					Constants.MESSAGE_SUBJECT));
			countMessages.put(
					(String) response.getMessage().get(
							Constants.MESSAGE_SUBJECT), count + 1);
		}
	}
	
	class MultipleMessagesTestHandler extends SimpleChannelUpstreamHandler {

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			BulkMessageRetrieveResponse response = (BulkMessageRetrieveResponse) e
					.getMessage();
			for (Message msg : response.getMessages()) {
				receivedMessages.put(
						(String) msg.get(Constants.MESSAGE_SUBJECT), msg);
			}
		}
	}


}
