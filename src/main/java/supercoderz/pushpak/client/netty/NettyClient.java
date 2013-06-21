package supercoderz.pushpak.client.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import supercoderz.pushpak.client.AbstractClient;
import supercoderz.pushpak.client.ClientException;
import supercoderz.pushpak.client.IClientMessageHandler;
import supercoderz.pushpak.message.BulkMessageRetrieveResponse;
import supercoderz.pushpak.message.Response;
import supercoderz.pushpak.utils.LogUtils;

public class NettyClient extends AbstractClient {

	ClientBootstrap bootstrap;
	ChannelFuture future;
	IClientMessageHandler handler;

	public void connect(String hostname, int port, IClientMessageHandler handler)
			throws ClientException {
		try {
			if (handler == null) {
				throw new ClientException("Invalid client handler value "
						+ handler);
			}
			this.handler = handler;
			LogUtils.info("NettyClient", "Connecting to server on " + hostname
					+ ":" + port);
			// Configure the client.
			bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));
			LogUtils.debug("NettyClient", "Created netty client bootstrap");
			// Set up the pipeline factory.
			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() throws Exception {
					return Channels.pipeline(new ObjectEncoder(),
							new ObjectDecoder(), new ClientHandler());
				}
			});
			LogUtils.debug("NettyClient",
					"Assigned upstream handler for messages");

			// Start the connection attempt.
			future = bootstrap.connect(new InetSocketAddress(hostname, port));

			// wait for connection
			future.await();
			LogUtils.info("NettyClient", "Connected to server on " + hostname
					+ ":" + port);
		} catch (Exception e) {
			LogUtils.error("NettyClient", "Error connecting to server on "
					+ hostname + ":" + port + "\n" + e.getMessage());
			throw new ClientException(e);
		}
	}

	public void disconnect() throws ClientException {
		try {
			LogUtils.info("NettyClient", "Disconnecting client from server");
			future.getChannel().close();
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public void send(Object message) {
		future.getChannel().write(message);
	}

	class ClientHandler extends SimpleChannelUpstreamHandler {
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
			LogUtils.error("NettyClient", "Error in client connection "
					+ e.getCause().getMessage());
			super.exceptionCaught(ctx, e);
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			Object message = e.getMessage();
			if (message instanceof Response) {
				handler.onMessage(((Response) message).getMessage());
			} else if (message instanceof BulkMessageRetrieveResponse) {
				handler.handleMessages(((BulkMessageRetrieveResponse) message)
						.getMessages());
			}
		}
	}

}
