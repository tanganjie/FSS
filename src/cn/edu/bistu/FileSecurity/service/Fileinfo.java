/**
 * 
 */
package cn.edu.bistu.FileSecurity.service;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import cn.edu.bistu.FileSecurity.model.User;
import cn.edu.bistu.FileSecurity.util.MongodbTool;
import cn.edu.bistu.FileSecurity.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author sunxinwei
 *
 */
@Path("/fileinfo")
public class Fileinfo {
	static Logger logger = Logger.getLogger(Fileinfo.class.getName());
	
	/**
	 * 查询用户文件列表
	 * @param request
	 * @return
	 */
	@POST
	@Path("/sysfile")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray querySysFileInfo(@Context HttpServletRequest request){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			MongoDatabase db = client.getDatabase(Tools.getProperties().getProperty("database"));
			MongoCollection<Document> files = db.getCollection("fileinfo");
			HttpSession session = request.getSession();
			logger.info("sessionid: " + session.getId());
			User user = (User) session.getAttribute("login_user");
			if(user == null){
				return null;
			}
			logger.info("user:" + user.get_id());
			
			JSONArray json = new JSONArray();
			MongoCursor<Document> iterater = files.find(Filters.eq("user", user.get_id())).iterator();
			while(iterater.hasNext()){
				Document doc = iterater.next();
				logger.info(doc.toJson());
				JSONObject object = JSONObject.fromObject(doc.toJson());
				json.add(object);
			}
			iterater.close();
			return json;
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			client.close();
		}
		return null;
	}
	
	/**
	 * 文件信息
	 * @param filename
	 * @param request
	 * @return
	 */
	@POST
	@Path("/filedetail")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject queryFileDetail(@FormParam("filename") String filename, @Context HttpServletRequest request){
		MongoClient client = MongodbTool.getMongoClient();
			try {
				MongoDatabase db = client.getDatabase(Tools.getProperties().getProperty("database"));
				HttpSession session = request.getSession();
				User user = (User) session.getAttribute("login_user");
				MongoCollection<Document> files = db.getCollection("fileinfo");
				MongoCollection<Document> clouds = db.getCollection("cloudinfo");
				
				if(files.count(Filters.and(Filters.eq("filename", filename), Filters.eq("user", user.get_id()))) == 0)
					return null;
				Document doc = files.find(Filters.and(Filters.eq("filename", filename), Filters.eq("user", user.get_id()))).first();
				String fileid = doc.getString("_id");
				MongoCursor<Document> cursor = clouds.find(Filters.eq("fileinfo", fileid)).iterator();
				HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();
				while(cursor.hasNext()){
					Document split = cursor.next();
					JSONArray array = null;
					if(map.get(split.getString("cloud")) == null){
						array = new JSONArray();
						map.put(split.getString("cloud"), array);
					}else{
						array = map.get(split.getString("cloud"));
					}
					array.add(split.toJson());
				}
				StringBuilder json = new StringBuilder();
				json.append("{\"file\":")
					.append(doc.toJson())
					.append(",\"splits\":")
					.append(JSONObject.fromObject(map))
					.append("}");
				logger.info("json: " + json.toString());
				return JSONObject.fromObject(json.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();
		map.put("aaa", JSONArray.fromObject("[{\"id\":\"11\"},{\"id\":\"1231\"}]"));
		map.put("xxx", JSONArray.fromObject("[{\"id\":\"13331\"},{\"id\":\"1231231\"}]"));
		System.out.println(JSONObject.fromObject(map).toString());
//		MongoClient client = MongodbTool.getMongoClient();
//		MongoDatabase db = client.getDatabase(Tools.getProperties().getProperty("database"));
//		MongoCollection<Document> files = db.getCollection("fileinfo");
//		String str = files.find(Filters.eq("user", "55e3c75d38e2e87101a0c7f8")).first().toJson();
//		System.out.println(str);
	}
}
