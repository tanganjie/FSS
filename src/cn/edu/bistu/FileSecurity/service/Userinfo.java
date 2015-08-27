/**
 * 
 */
package cn.edu.bistu.FileSecurity.service;

import java.io.IOException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import net.sf.json.JSONArray;
import cn.edu.bistu.FileSecurity.model.*;
import cn.edu.bistu.FileSecurity.util.MongodbTool;
import cn.edu.bistu.FileSecurity.util.Tools;

/**
 * @author sunxinwei
 *
 */
@Path("/hello")
public class Userinfo {
	static Logger logger = Logger.getLogger(Userinfo.class.getName());
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayHello() {
		User user = new User(1, "tanganjie", "root", "taj", "t_anjie@qq.com");
		JSONArray json = JSONArray.fromObject(user);
		logger.info(json.toString());
		return json.toString();
	}
	
	public String adduser(){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			String databasename = Tools.getProperties().getProperty("database");
			MongoDatabase database = client.getDatabase(databasename);
			MongoCollection<Document> users = database.getCollection("userinfo");
			Document user = new Document();
		} catch (IOException e) {
			return "error";
		}
		finally{
			client.close();
		}
		return "success";
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		User user = new User(1, "tanganjie", "root", "taj", "t_anjie@qq.com");
		JSONArray json = JSONArray.fromObject(user);
		logger.info(json.toString());
	}
}
