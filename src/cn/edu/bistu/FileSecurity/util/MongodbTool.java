package cn.edu.bistu.FileSecurity.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.*;

import com.mongodb.*;

public class MongodbTool {
	static Logger logger = Logger.getLogger(MongodbTool.class.getName());
	
	public static MongoClient getMongoClient() {
		try {
			logger.info("Loading config.properties...");
			Properties properties = Tools.getProperties();
			logger.info("Loading config.properties...Ok");
			
			String host = properties.getProperty("host");
			int port = Integer.parseInt(properties.getProperty("port"));
			return new MongoClient(host, port);
		} catch (IOException e) {
			logger.info("Loading config.properties...Failed");
			return null;
		}
	}
	
	
	//public 
	public static void main(String[] args) {
		if(MongodbTool.getMongoClient() == null){
			logger.info("connect failed");
		}
		else{
			logger.info("connect ok");
		}
	}

}
