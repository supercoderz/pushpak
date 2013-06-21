package supercoderz.pushpak.client.mina;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import supercoderz.pushpak.client.AbstractClient;
import supercoderz.pushpak.client.ClientException;
import supercoderz.pushpak.client.IClientMessageHandler;
import supercoderz.pushpak.message.BulkMessageRetrieveResponse;
import supercoderz.pushpak.message.Response;
import supercoderz.pushpak.utils.LogUtils;

public class MinaClient extends AbstractClient {

	IClientMessageHandler handler;
	IoSession session;
	private SocketConnector connector;

	public void connect(String hostname, int port, IClientMessageHandler handler)
			throws ClientException {
		try {
			if (handler == null) {
				throw new ClientException("Invalid client handler value "
						+ handler);
			}
			this.handler = handler;
			LogUtils.info("MinaClient", "Connecting to server on " + hostname
					+ ":" + port);
			connector = new NioSocketConnector();
			LogUtils.debug("MinaClient", "Created netty client bootstrap");
			// Set up the pipeline factory.
			LogUtils.debug("MinaClient",
					"Assigned upstream handler for messages");
			connector.setHandler(new ClientHandler());
			connector.getSessionConfig().setSendBufferSize(2048);
			connector.getFilterChain().addLast(
					"codec",
					new ProtocolCodecFilter(
							new ObjectSerializationCodecFactory()));
			ConnectFuture future = connector.connect(
					new InetSocketAddress(hostname, port)).await();
			session = future.getSession();

			// wait for connection
			LogUtils.info("MinaClient", "Connected to server on " + hostname
					+ ":" + port);
		} catch (Exception e) {
			LogUtils.error("MinaClient", "Error connecting to server on "
					+ hostname + ":" + port + "\n" + e.getMessage());
			throw new ClientException(e);
		}
	}

	public void disconnect() throws ClientException {
		try {
			LogUtils.info("MinaClient", "Disconnecting client from server");
			session.close(true);
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public void send(Object message) {
		session.write(message);
	}

	class ClientHandler extends IoHandlerAdapter {

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			LogUtils.error("MinaClient",
					"Error in client connection " + cause.getMessage());
			super.exceptionCaught(session, cause);
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			if (message instanceof Response) {
				handler.onMessage(((Response) message).getMessage());
			} else if (message instanceof BulkMessageRetrieveResponse) {
				handler.handleMessages(((BulkMessageRetrieveResponse) message)
						.getMessages());
			}
		}
	}

}
