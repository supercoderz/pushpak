package supercoderz.pushpak.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import supercoderz.pushpak.server.ServerException;

public abstract class AbstractServerStarter {

	Properties properties = new Properties();
	boolean init = false;

	public AbstractServerStarter() throws FileNotFoundException, IOException,
			ServerException {
		// check if we have a conf file
		String conf_file = System.getProperty("config");
		if (conf_file != null) {
			properties.load(new FileReader(conf_file));
			init = true;
		} else {
			String port = System.getProperty("port");
			String data_dir = System.getProperty("data.dir");
			String cache_size = System.getProperty("cache.size");
			if (port != null || data_dir != null) {
				properties.put("port", port);
				properties.put("data.dir", data_dir);
				properties.put("cache.size", cache_size);
				init = true;
			}
		}
		if (!init) {
			throw new ServerException("Did not find startup parameters");
		}
	}

	public abstract void start() throws IOException;

}
