package supercoderz.pushpak.server.mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import supercoderz.pushpak.utils.LogUtils;

public class PushpakMinaServer {

	int port;
	SocketAcceptor acceptor;

	public PushpakMinaServer(int port, String baseDir, int maxCacheEntries) {
		LogUtils.info("PushpakServer", "Initializing pushpak Server");
		LogUtils.debug("PushpakServer", "Initializing netty for pushpak Server");
		this.port = port;
		acceptor = new NioSocketAcceptor();
		acceptor.setHandler(new PushpakMinaServerHandler(baseDir,
				maxCacheEntries));
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		LogUtils.debug("PushpakServer",
				"Completed initializing netty for pushpak Server");
		LogUtils.info("PushpakServer", "Completed initializing pushpak Server");

	}

	public void startServer() throws IOException {
		LogUtils.info("PushpakServer", "Starting PushpakServer using on port "
				+ port);
		// Bind and start to accept incoming connections.
		acceptor.bind(new InetSocketAddress(port));
		// add a shutdown hook - to handle where it is not shutdown properly
		LogUtils.debug("PushpakServer",
				"Adding shutdown hook for server shutdown");
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				stopServer();
			}
		}));
	}

	public void stopServer() {
		LogUtils.info("PushpakServer", "Initiating shutdown for PushpakServer");
		// this will call the handler close which will in turn close all
		// the other components like the file persistence etc
		acceptor.unbind();
	}

}
