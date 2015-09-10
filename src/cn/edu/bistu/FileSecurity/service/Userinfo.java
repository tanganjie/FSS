/**
 * 
 */
package cn.edu.bistu.FileSecurity.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.*;

import net.sf.json.JSONArray;
import cn.edu.bistu.FileSecurity.model.*;
import cn.edu.bistu.FileSecurity.util.MongodbTool;
import cn.edu.bistu.FileSecurity.util.Tools;

/**
 * @author sunxinwei 用户信息服务
 *
 */
@Path("/userinfo")
public class Userinfo {
	static Logger logger = Logger.getLogger(Userinfo.class.getName());

	/**
	 * 注册
	 * 
	 * @param username
	 * @param password
	 * @param passwordconfirm
	 * @param nickname
	 * @param email
	 * @return message
	 */
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String register(@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("passwordconfirm") String passwordconfirm,
			@FormParam("nickname") String nickname,
			@FormParam("email") String email) {
		User user = new User(new ObjectId().toString(), username, password,
				nickname, email);
		logger.info(user.toString());
		MongoClient client = MongodbTool.getMongoClient();
		try {
			String databasename = Tools.getProperties().getProperty("database");
			MongoDatabase database = client.getDatabase(databasename);
			MongoCollection<Document> users = database.getCollection("userinfo");
			logger.info("getCollection userinfo Success...");
			users.insertOne(user.parseMongoDoc());
			logger.info("insert userinfo Success...");
		} catch (IOException e) {
			logger.info(e);
			return "error";
		} finally {
			client.close();
			logger.info("client closed");
		}
		return "success";
	}

	/**
	 * 验证注册用户名是否存在
	 * 
	 * @param username
	 * @return message
	 */
	@POST
	@Path("/registerValidUsername")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String validRegisterUsername(@FormParam("username") String username) {
		logger.info("Username: " + username);
		MongoClient client = MongodbTool.getMongoClient();
		try {
			String databasename = Tools.getProperties().getProperty("database");
			MongoDatabase database = client.getDatabase(databasename);
			MongoCollection<Document> users = database.getCollection("userinfo");
			logger.info("getCollection userinfo Success...");
			if (users.count(Filters.eq("username", username)) != 0) {
				logger.info("Username already exists...");
				return "invalid";
			}
		} catch (IOException e) {
			logger.info(e);
			return "error";
		} finally {
			client.close();
			logger.info("client closed");
		}
		return "success";
	}

	/**
	 * 登录
	 * 
	 * @param username
	 * @param password
	 * @param request
	 * @return message
	 */
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public User login(
			@FormParam("username") String username,
			@FormParam("password") String password,
			@Context HttpServletRequest request) {
		logger.info("Username: " + username + " Password: " + password);
		// 获取client
		MongoClient client = MongodbTool.getMongoClient();
		try {
			String databasename = Tools.getProperties().getProperty("database");
			MongoDatabase database = client.getDatabase(databasename);
			MongoCollection<Document> users = database.getCollection("userinfo");
			logger.info("getCollection userinfo Success...");
			if (users.count(Filters.and(Filters.eq("username", username),Filters.eq("password", password))) == 1) {
				logger.info("Login success!");
				Document doc = users.find(Filters.and(Filters.eq("username", username),Filters.eq("password", password))).first();
				User user = new User();
				user.parseUser(doc);
				HttpSession session = request.getSession();
				logger.info("SessionId :" + session.getId());
				session.setAttribute("login_user", user);
				return user;
			}
		} catch (IOException e) {
			logger.info(e);
		} finally {
			client.close();
			logger.info("client closed");
		}
		return null;
	}

	/**
	 * 获取登录session信息
	 * @param request
	 * @return message
	 */
	@POST
	@Path("/sessioninfo")
	@Produces(MediaType.APPLICATION_JSON)
	public User SessionInfo(@Context HttpServletRequest request){
		HttpSession session = request.getSession();
		logger.info("SessionId :" + session.getId());
		if(session.getAttribute("login_user") != null){
			User user = (User)session.getAttribute("login_user");
			MongoClient client = MongodbTool.getMongoClient();
			try {
				//验证session中密码是否正确
				String databasename = Tools.getProperties().getProperty("database");
				MongoDatabase database = client.getDatabase(databasename);
				MongoCollection<Document> users = database.getCollection("userinfo");
				logger.info("getCollection userinfo Success...");
				Document doc = users.find(Filters.eq("username", user.getUsername())).first();
				String password = doc.getString("password");
				if(password.equals(user.getPassword())){
					logger.info("login info: " + user.toString());
					return user;
				}
			} catch (IOException e) {
				logger.info(e);
			} finally {
				client.close();
				logger.info("client closed");
			}
		}
		logger.info("no login info");
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		User user = new User("1", "tanganjie", "root", "taj", "t_anjie@qq.com");
		JSONArray json = JSONArray.fromObject(user);
		logger.info(json.toString());
		String id = "aaa";
		logger.info(new ObjectId());
		logger.info(new ObjectId());
		if("111" == "111"){
			logger.info("!!!");
		}
	}
}
