package supercoderz.pushpak.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import supercoderz.pushpak.server.ServerException;
import supercoderz.pushpak.server.mina.PushpakMinaServer;


public class MinaServerStarter extends AbstractServerStarter {

	PushpakMinaServer server;

	public MinaServerStarter() throws FileNotFoundException, IOException,
			ServerException {
		super();
	}

	@Override
	public void start() throws IOException {
		server = new PushpakMinaServer(Integer.parseInt(properties
				.getProperty("port")), properties.getProperty("data.dir"),
				Integer.parseInt(properties.getProperty("cache.size")));
		// shut down is taken care by the shutdown hook in the server class
		server.startServer();
	}

	public static void main(String[] args) {
		try {
			MinaServerStarter starter = new MinaServerStarter();
			starter.start();
		} catch (ServerException se) {
			System.out
					.println("Usage:\n"
							+ "java -Dconf=<config file> MinaServerStarter (or)\n"
							+ "java -Dport-<port> -Ddata.dir-<data dir> -Dcache.size=<cache size> MinaServerStarter\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
