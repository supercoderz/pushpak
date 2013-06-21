package supercoderz.pushpak.utils;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

public class LogUtils {
	static	{
		//check for file or property - if none exist then use basic
		File file = new File("log4j.properties");
		String prop = System.getenv("log4j.configuration");
		if(file.exists()){
			PropertyConfigurator.configure("log4j.properties");
		}else if(prop!=null){
				PropertyConfigurator.configure(prop);
		}else{
			BasicConfigurator.configure();
		}		
	}
	
	public static void debug(String logger,String message){
		Logger.getLogger(Constants.ROOT_LOGGER+logger).debug(message);
	}

	public static void info(String logger,String message){
		Logger.getLogger(Constants.ROOT_LOGGER+logger).info(message);
	}
	
	public static void warn(String logger,String message){
		Logger.getLogger(Constants.ROOT_LOGGER+logger).warn(message);
	}
	
	public static void error(String logger,String message){
		Logger.getLogger(Constants.ROOT_LOGGER+logger).error(message);
	}
	
	}
