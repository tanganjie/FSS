package cn.edu.bistu.FileSecurity.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Tools {
	final protected static String propFilePath = "/config.properties";
	static Logger logger = Logger.getLogger(MongodbTool.class.getName());
	
	public static Properties getProperties() throws IOException{
		Properties prop = new Properties();
		InputStream in = Class.class.getResourceAsStream(propFilePath);
		prop.load(in);
		return prop;
	}
	public static void main(String[] args) {
		try {
			Tools.getProperties();
			logger.info("success load the property");
		} catch (IOException e) {
			logger.info("can not find the property file: " + propFilePath);
		}
	}
}
