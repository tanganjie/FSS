/**
 * 
 */
package cn.edu.bistu.FileSecurity.db;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import cn.edu.bistu.FileSecurity.model.CloudFile;
import cn.edu.bistu.FileSecurity.model.User;
import cn.edu.bistu.FileSecurity.util.MongodbTool;
import cn.edu.bistu.FileSecurity.util.Tools;

/**
 * @author sunxinwei
 *
 */
public class FileInfoOperation {
	static Logger logger = Logger.getLogger(FileInfoOperation.class.getName());
	
	/**
	 * 保存上传信息
	 * @param map
	 * @return
	 */
	public boolean saveCloudInfo(HashMap<String, List<CloudFile>> map, String sourceFilename){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			MongoDatabase database = client.getDatabase(Tools.getProperties().getProperty("database"));
			MongoCollection<Document> files = database.getCollection("fileinfo");
			MongoCollection<Document> clouds = database.getCollection("cloudinfo");
			Iterator iterator = map.entrySet().iterator();
			logger.info("keyName: " + sourceFilename);
			Document fileDoc = null;
			if(files.count(Filters.eq("filename", sourceFilename)) == 1){
				fileDoc = files.find(Filters.eq("filename", sourceFilename)).first();
			}
			else
				return false;
			while(iterator.hasNext()){
				Entry<String, List<CloudFile>> entry = (Entry) iterator.next();
				String key = entry.getKey();
				logger.info("keyName: " + key);
				List<CloudFile> valueList = entry.getValue();
				for(CloudFile cf:valueList){
					Document doc = new Document()
							.append("_id", new ObjectId().toString())
							.append("index", cf.getIndex())
							.append("name", cf.getFilename())
							.append("uploadtime", cf.getUploadDate())
							.append("cid", cf.getId())
							.append("cloud", cf.getCloudname())
							//.append("fileinfo", new DBRef("fileinfo", fileDoc.get("_id")));
							.append("fileinfo", fileDoc.get("_id"));
					clouds.insertOne(doc);
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 检查文件名是否重复并获取重复文件名的别称
	 * @param source
	 * @return
	 */
	public String getFileName(String source){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			MongoDatabase database = client.getDatabase(Tools.getProperties().getProperty("database"));
			MongoCollection<Document> files = database.getCollection("fileinfo");
			int index = 1;
			String type = "";
			String name = source;
			if(source.lastIndexOf('.') != -1){
				type = source.substring(source.lastIndexOf('.') + 1);
				name = source.substring(0, source.lastIndexOf('.'));
			}
			logger.info("file type: " + type + " name: " + name);
			while(files.count(Filters.eq("filename", source)) == 1){
				source = name + "(" + index + ")" + "." + type;
				index++;
			}
			logger.info("source: " + source);
			return source;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		return null;
	}
	
	
	/**
	 * 文件上传
	 * @param filename
	 * @param path
	 * @param user
	 */
	public boolean uploadFileInfo(String filename, String path, User user){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			String databasename = Tools.getProperties().getProperty("database");
			MongoDatabase database = client.getDatabase(databasename);
			MongoCollection<Document> files = database.getCollection("fileinfo");
			logger.info("getCollection userinfo Success...");
			
			String type = "";
			String[] strs = filename.split("\\.");
			if(strs.length != 1){
				type = strs[strs.length - 1];
			}

			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String dateStr = dt1.format(date);
			
			Document doc = new Document()
					.append("_id", new ObjectId().toString())
					.append("type", type)
					.append("filename", filename)
					.append("filepath", path)
					.append("status", 0)
					.append("uploadDate", dateStr)
					//.append("user", new DBRef("userinfo", user.get_id()))
					.append("user", user.get_id());
			files.insertOne(doc);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		return false;
	}
	
	/**
	 * 获取文件存储路径
	 * @param filename
	 * @return
	 */
	public String getFilePath(String filename, User user){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			MongoDatabase database = client.getDatabase(Tools.getProperties().getProperty("database"));
			MongoCollection<Document> files = database.getCollection("fileinfo");
			if(files.count(Filters.and(Filters.eq("filename", filename), Filters.eq("user", user.get_id()))) > 0){
				Document doc = files.find(Filters.and(Filters.eq("filename", filename), Filters.eq("user", user.get_id()))).first();
				return doc.getString("filepath");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			client.close();
		}
		return null;
	}
	
	/**
	 * 删除文件信息
	 * @param filename
	 * @param user
	 */
	public void deleteFileInfo(String filename, User user){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			MongoDatabase database = client.getDatabase(Tools.getProperties().getProperty("database"));
			MongoCollection<Document> files = database.getCollection("fileinfo");
			
			files.deleteMany(Filters.and(Filters.eq("filename", filename), Filters.eq("user", user.get_id())));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
	}
	
	/**
	 * 获取文件名
	 * @param fileid
	 * @return
	 */
	public String getFilename(String fileid){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			MongoDatabase database = client.getDatabase(Tools.getProperties().getProperty("database"));
			MongoCollection<Document> files = database.getCollection("fileinfo");
			if(files.count(Filters.eq("_id", fileid)) > 0){
				return files.find(Filters.eq("_id", fileid)).first().getString("filename");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取云文件信息
	 * @param index
	 * @param fileid
	 * @return
	 */
	public List<CloudFile> queryCloudInfo(int index, String fileid){
		MongoClient client = MongodbTool.getMongoClient();
		try {
			List<CloudFile> list = new ArrayList<CloudFile>();
			MongoDatabase database = client.getDatabase(Tools.getProperties().getProperty("database"));
			MongoCollection<Document> clouds = database.getCollection("cloudinfo");
			MongoCursor<Document> cursor = clouds.find(Filters.and(Filters.eq("index", index), Filters.eq("fileinfo", fileid))).iterator();
			while(cursor.hasNext()){
				Document doc = cursor.next();
				list.add(new CloudFile(doc.getInteger("index"), doc.getString("cloud"), doc.getString("name"), doc.getString("uploadtime"), doc.getString("cid")));
			}
			if(list.size() == 0 )
				return null;
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			client.close();
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dateStr = dt1.format(date);
		logger.info(dateStr);
		
		FileInfoOperation op = new FileInfoOperation();
		op.getFileName("dfasdkjlf");
		//logger.info(date.toString());
	}

}
