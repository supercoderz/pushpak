package supercoderz.pushpak.server.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import supercoderz.pushpak.utils.LogUtils;

public class PushpakNettyServer {
	ServerBootstrap bootstrap;
	int port;
	Channel channel;
	PushpakNettyServerHandler handler;

	public PushpakNettyServer(int port, String baseDir, int maxCacheEntries) {
		LogUtils.info("PushpakServer", "Initializing pushpak Server");
		LogUtils.debug("PushpakServer", "Initializing netty for pushpak Server");
		handler = new PushpakNettyServerHandler(baseDir, maxCacheEntries);
		this.port = port;
		bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new ObjectEncoder(),
						new ObjectDecoder(), handler);
			}
		});
		LogUtils.debug("PushpakServer",
				"Completed initializing netty for pushpak Server");
		LogUtils.info("PushpakServer", "Completed initializing pushpak Server");

	}

	public void startServer() {
		LogUtils.info("PushpakServer", "Starting PushpakServer using on port "
				+ port);
		// Bind and start to accept incoming connections.
		channel = bootstrap.bind(new InetSocketAddress(port));
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
		channel.close();
	}

}
