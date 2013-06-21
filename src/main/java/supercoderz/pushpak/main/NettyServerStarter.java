package supercoderz.pushpak.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import supercoderz.pushpak.server.ServerException;
import supercoderz.pushpak.server.netty.PushpakNettyServer;


public class NettyServerStarter extends AbstractServerStarter {

	PushpakNettyServer server;

	public NettyServerStarter() throws FileNotFoundException, IOException,
			ServerException {
		super();
	}

	@Override
	public void start() throws IOException {
		server = new PushpakNettyServer(Integer.parseInt(properties
				.getProperty("port")), properties.getProperty("data.dir"),
				Integer.parseInt(properties.getProperty("cache.size")));
		// shut down is taken care by the shutdown hook in the server class
		server.startServer();
	}

	public static void main(String[] args) {
		try {
			NettyServerStarter starter = new NettyServerStarter();
			starter.start();
		} catch (ServerException se) {
			System.out
					.println("Usage:\n"
							+ "java -Dconf=<config file> NettyServerStarter (or)\n"
							+ "java -Dport-<port> -Ddata.dir-<data dir> -Dcache.size=<cache size> NettyServerStarter\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
