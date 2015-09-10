/**
 * 
 */
package cn.edu.bistu.FileSecurity.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import cn.edu.bistu.FileSecurity.db.FileInfoOperation;
import cn.edu.bistu.FileSecurity.model.CloudFile;
import cn.edu.bistu.FileSecurity.model.User;
import cn.edu.bistu.FileSecurity.security.FileSecurity;
import cn.edu.bistu.FileSecurity.security.in.Cloud;
import net.sf.json.JSONArray;

/**
 * @author sunxinwei
 *
 */
@Path("/filetool")
public class Filetool {
	static Logger logger = Logger.getLogger(Filetool.class.getName());
	protected static final String path = Filetool.class.getResource("/../../media/").getPath();
	protected FileInfoOperation operation = new FileInfoOperation();

//	@GET
//	@Path("/path")
//	@Produces(MediaType.APPLICATION_JSON)
//	public JSONArray getPath() {
//		User user = new User("1", "tanganjie", "root", "taj", "t_anjie@qq.com");
//		JSONArray json = JSONArray.fromObject(user);
//		return json;
//	}

	@POST
	@Path("/download")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@FormParam("fileid") String fileid, @Context HttpServletRequest request){
		String filename = operation.getFilename(fileid);
		String preStr = path + "temp/";
		HashMap<Integer, String> map = new HashMap<>();
		if(filename != null){
			for(int i = 1; i <= 4; i++){
				List<CloudFile> list = operation.queryCloudInfo(i, fileid);
				if(list.size() == 0)
					return Response.status(404).entity("FILE NOT FOUND!").type("text/plain").build();
				String path = FileSecurity.dowloadFromCloud(list, preStr);
				if(path == null){
					return Response.status(404).entity("FILE NOT FOUND!").type("text/plain").build();
				}
				map.put(i, path);
			}
			String newFilename = UUID.randomUUID().toString();
			String newFilePath = path + newFilename;
			try {
				FileSecurity.decrypt(newFilePath, map, preStr);
				File file = new File(newFilePath);
				ResponseBuilder builder = Response.ok(file).header("Content-Disposition", "attachment; filename=" + filename);
				return builder.build();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Response.status(404).entity("FILE NOT FOUND!").type("text/plain").build();
	}
	
	/**
	 * 上传分片到云
	 * @param param
	 * @param request
	 * @return
	 */
	@POST
	@Path("/uploadToCloud")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String splitFileAndUploadToCloud(@FormParam("param") String param, @Context HttpServletRequest request) {
		logger.info("param: " + param);
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("login_user");
		String[] params = param.split("@");
		String filename = params[0];
		String filepath = operation.getFilePath(filename, user);
		String preStr = path + "temp/";
		try {
			//拆分
			HashMap<Integer, String> filemap = FileSecurity.encrypt(filepath, preStr);
			//云列表
			String[] clouds = params[1].split("#");
			HashMap<Integer, Integer> cloudMap = new HashMap<>();
			logger.info("length: " + clouds.length);
			for(int i = 1, j = 1; i <= clouds.length; i++){
				logger.info("i: " + clouds[i-1]);
				if(clouds[i-1].equals("true")){
					cloudMap.put(j, i);
					j++;
				}
			}
			logger.info("cloudsize: " + cloudMap.size());
			logger.info("filesize: " + filemap.size());
			//上传云
			HashMap<String, List<CloudFile>> map = FileSecurity.uploadToCloud(filemap, cloudMap, preStr);
			Iterator<Entry<Integer, String>> iter = filemap.entrySet().iterator();
			while(iter.hasNext()){
				Entry<Integer, String> entry = iter.next();
				new File(preStr + entry.getValue()).delete();
			}
			if(map == null){
				operation.deleteFileInfo(filename, user);
				logger.info("map is null!!!!!!!!!");
				return "error";
			}
			//存库
			if(operation.saveCloudInfo(map, filename))
				return "success";
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			new File(filepath).delete();
		}
		return "error";
	}
	
	/**
	 * 文件上传
	 * @param fileInputStream
	 * @param fileFormDataContentDisposition
	 * @param request
	 * @return
	 */
	@POST
	@Path("/uploadfile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(
			@FormDataParam("files[]") InputStream fileInputStream,
			@FormDataParam("files[]") FormDataContentDisposition fileFormDataContentDisposition,
			@FormDataParam("clouds") String clouds,
			@Context HttpServletRequest request) {
		//logger.info(timeid);
		HttpSession session = request.getSession();
		logger.info("sessionid: " + session.getId());
		User user = (User) session.getAttribute("login_user");
		if(user == null){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("no user info").build();
			//return Response.ok().build();
		}
		logger.info(user.toString());
		String filename = operation.getFileName(fileFormDataContentDisposition.getFileName());
		try {
			String uploadPath = this.writeToFileServer(fileInputStream, user.getUsername(), filename);
			logger.info("uploadPath: " + uploadPath);
			operation.uploadFileInfo(filename, uploadPath, user);
			//String preStr = path + "temp/";
			//HashMap<Integer, String> map = FileSecurity.encrypt(uploadPath, preStr);
//			if(!operation.uploadFileInfo(filename, uploadPath, user)){
//				new File(uploadPath).delete();
//				return "error";
//			}
			logger.info("upload sucess");
			return Response.ok(filename + "@" + clouds).build();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("uploadFailed").build();
		//return Response.ok().build();
	}

	/**
	 * 上传文件流写入服务器
	 * 
	 * @param inputStream
	 * @param fileName
	 * @param username
	 * @return
	 * @throws IOException
	 */
	private String writeToFileServer(InputStream inputStream,String username, String fileName) throws IOException {
		OutputStream outputStream = null;
		String dir = path + username;
		File filedir = new File(dir);
		if(!filedir.exists()){
			filedir.mkdir();
		}
		String qualifiedUploadFilePath = dir + "/" + fileName;
		try {
			outputStream = new FileOutputStream(new File(qualifiedUploadFilePath));
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			outputStream.close();
		}
		return qualifiedUploadFilePath;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
}
