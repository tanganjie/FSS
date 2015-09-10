/**
 * 
 */
package cn.edu.bistu.FileSecurity.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.dropbox.core.DbxException;

import cn.edu.bistu.FileSecurity.db.FileInfoOperation;
import cn.edu.bistu.FileSecurity.model.CloudFile;
import cn.edu.bistu.FileSecurity.security.in.Cloud;

/**
 * @author sunxinwei
 *
 */
public class FileSecurity {
	static Logger logger = Logger.getLogger(FileSecurity.class.getName());
	protected FileInfoOperation operation = new FileInfoOperation();

	/**
	 * 拆分文件
	 * @param filepath
	 * @param preStr
	 * @return
	 * @throws IOException
	 */
	public static HashMap<Integer, String> encrypt(String filepath, String preStr) throws IOException {
		// 文件名
		String filename_part1 = UUID.randomUUID().toString();
		String filename_part2 = UUID.randomUUID().toString();
		String filename_part3 = UUID.randomUUID().toString();
		String filename_part4 = UUID.randomUUID().toString();

		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, filename_part1);
		map.put(2, filename_part2);
		map.put(3, filename_part3);
		map.put(4, filename_part4);

		// 拆分文件路径
		String file_part1 = preStr + filename_part1;
		String file_part2 = preStr + filename_part2;
		String file_part3 = preStr + filename_part3;
		String file_part4 = preStr + filename_part4;

		File file1 = new File(file_part1);
		File file2 = new File(file_part2);
		File file3 = new File(file_part3);
		File file4 = new File(file_part4);

		if (!file1.exists())
			file1.createNewFile();
		if (!file2.exists())
			file2.createNewFile();
		if (!file3.exists())
			file3.createNewFile();
		if (!file4.exists())
			file4.createNewFile();

		File sourceFile = new File(filepath);
		int length = (int) sourceFile.length();
		logger.info("length: " + length);
		FileInputStream in = new FileInputStream(filepath);
		byte[] file = new byte[1024 * 10];

		FileOutputStream out1 = new FileOutputStream(file_part1);
		FileOutputStream out2 = new FileOutputStream(file_part2);
		FileOutputStream out3 = new FileOutputStream(file_part3);
		FileOutputStream out4 = new FileOutputStream(file_part4);

		int byteread, realbyte = 0, exworld = 0;
		
		while ((byteread = in.read(file)) != -1) {
			int i;
			byte world1, world2, world3, world4, tem1, tem2, tem3, tem4;

			switch (byteread % 4) {
			case 0:
				realbyte = byteread;
				break;
			case 1:
				realbyte = byteread - 1;
				exworld = 1;
				break;
			case 2:
				realbyte = byteread - 2;
				exworld = 2;
				break;
			case 3:
				realbyte = byteread - 3;
				exworld = 3;
				break;
			default:
				break;
			}

			for (i = 0; i < realbyte; i = i + 4) {
				world1 = (byte) (file[i]);
				world2 = (byte) (file[i + 1]);
				world3 = (byte) (file[i + 2]);
				world4 = (byte) (file[i + 3]);
				tem1 = world1;
				tem1 = (byte) (tem1 >> 6);
				tem1 = (byte) (tem1 & 0x03);
				tem2 = world2;
				tem2 = (byte) (tem2 >> 4);
				tem2 = (byte) (tem2 & 0x0c);
				tem3 = world3;
				tem3 = (byte) (tem3 >> 2);
				tem3 = (byte) (tem3 & 0x30);
				tem4 = world4;
				tem4 = (byte) (tem4 & 0xc0);
				out1.write((byte) (tem1 + tem2 + tem3 + tem4));

				tem1 = world1;
				tem1 = (byte) (tem1 >> 4);
				tem1 = (byte) (tem1 & 0x03);
				tem2 = world2;
				tem2 = (byte) (tem2 >> 2);
				tem2 = (byte) (tem2 & 0x0c);
				tem3 = world3;
				tem3 = (byte) (tem3 & 0x30);
				tem4 = world4;
				tem4 = (byte) (tem4 << 2);
				tem4 = (byte) (tem4 & 0xc0);
				out2.write((byte) (tem1 + tem2 + tem3 + tem4));

				tem1 = world1;
				tem1 = (byte) (tem1 >> 2);
				tem1 = (byte) (tem1 & 0x03);
				tem2 = world2;
				tem2 = (byte) (tem2 & 0x0c);
				tem3 = world3;
				tem3 = (byte) (tem3 << 2);
				tem3 = (byte) (tem3 & 0x30);
				tem4 = world4;
				tem4 = (byte) (tem4 << 4);
				tem4 = (byte) (tem4 & 0xc0);
				out3.write((byte) (tem1 + tem2 + tem3 + tem4));

				tem1 = world1;
				tem1 = (byte) (tem1 & 0x03);
				tem2 = world2;
				tem2 = (byte) (tem2 << 2);
				tem2 = (byte) (tem2 & 0x0c);
				tem3 = world3;
				tem3 = (byte) (tem3 << 4);
				tem3 = (byte) (tem3 & 0x30);
				tem4 = world4;
				tem4 = (byte) (tem4 << 6);
				tem4 = (byte) (tem4 & 0xc0);
				out4.write((byte) (tem1 + tem2 + tem3 + tem4));
			}
			switch (exworld) {
			case 0:
				break;
			case 1:
				out1.write(file[realbyte]);
				break;
			case 2:
				out1.write(file[realbyte]);
				out1.write(file[realbyte + 1]);
				break;
			case 3:
				out1.write(file[realbyte]);
				out1.write(file[realbyte + 1]);
				out1.write(file[realbyte + 2]);
				break;
			default:
				break;
			}
		}
		in.close();
		out1.close();
		out2.close();
		out3.close();
		out4.close();
		return map;
	}

	/**
	 * 文件合成
	 * 
	 * @param filepath
	 * @param map
	 * @throws IOException
	 */
	public static void decrypt(String filepath, HashMap<Integer, String> map, String preStr) throws IOException {
		String file_part1 = map.get(1);
		String file_part2 = map.get(2);
		String file_part3 = map.get(3);
		String file_part4 = map.get(4);

		File file1 = new File(file_part1);
		File file2 = new File(file_part2);
		File file3 = new File(file_part3);
		File file4 = new File(file_part4);

		FileInputStream in1 = new FileInputStream(file_part1);
		FileInputStream in2 = new FileInputStream(file_part2);
		FileInputStream in3 = new FileInputStream(file_part3);
		FileInputStream in4 = new FileInputStream(file_part4);

		FileOutputStream out1 = new FileOutputStream(filepath);

		int realbyte = 0, exworld = 0;
		int readbyte = (int) file1.length();
		int readf2 = (int) file2.length();

		switch (readbyte - readf2) {
		case 0:
			realbyte = readbyte;
			exworld = 0;
			break;
		case 1:
			realbyte = readbyte - 1;
			exworld = 1;
			break;
		case 2:
			realbyte = readbyte - 2;
			exworld = 2;
			break;
		case 3:
			realbyte = readbyte - 3;
			exworld = 3;
			break;
		default:
			break;
		}
		byte[] f1world = new byte[realbyte + 4];
		byte[] f2world = new byte[realbyte];
		byte[] f3world = new byte[realbyte];
		byte[] f4world = new byte[realbyte];
		byte[] tempbytes = new byte[realbyte * 4];
		in1.read(f1world);
		in2.read(f2world);
		in3.read(f3world);
		in4.read(f4world);
		byte world1, world2, world3, world4, tem1, tem2, tem3, tem4;
		int i, j;

		for (i = 0, j = 0; i < realbyte; i++, j = j + 4) {
			tem1 = (byte) (f1world[i]);
			tem2 = (byte) (f2world[i]);
			tem3 = (byte) (f3world[i]);
			tem4 = (byte) (f4world[i]);
			world1 = tem1;
			world2 = tem2;
			world3 = tem3;
			world4 = tem4;
			tem1 = (byte) (tem1 << 6);
			tem1 = (byte) (tem1 & 0xc0);
			tem2 = (byte) (tem2 << 4);
			tem2 = (byte) (tem2 & 0x30);
			tem3 = (byte) (tem3 << 2);
			tem3 = (byte) (tem3 & 0x0c);
			tem4 = (byte) (tem4 & 0x03);
			tempbytes[j] = (byte) (tem1 + tem2 + tem3 + tem4);
			out1.write(tempbytes[j]);

			tem1 = world1;
			tem2 = world2;
			tem3 = world3;
			tem4 = world4;

			tem1 = (byte) (tem1 << 4);
			tem1 = (byte) (tem1 & 0xc0);
			tem2 = (byte) (tem2 << 2);
			tem2 = (byte) (tem2 & 0x30);
			tem3 = (byte) (tem3 & 0x0c);
			tem4 = (byte) (tem4 >> 2);
			tem4 = (byte) (tem4 & 0x03);
			tempbytes[j + 1] = (byte) (tem1 + tem2 + tem3 + tem4);
			out1.write(tempbytes[j + 1]);

			tem1 = world1;
			tem2 = world2;
			tem3 = world3;
			tem4 = world4;

			tem1 = (byte) (tem1 << 2);
			tem1 = (byte) (tem1 & 0xc0);
			tem2 = (byte) (tem2 & 0x30);
			tem3 = (byte) (tem3 >> 2);
			tem3 = (byte) (tem3 & 0x0c);
			tem4 = (byte) (tem4 >> 4);
			tem4 = (byte) (tem4 & 0x03);
			tempbytes[j + 2] = (byte) (tem1 + tem2 + tem3 + tem4);
			out1.write(tempbytes[j + 2]);

			tem1 = world1;
			tem2 = world2;
			tem3 = world3;
			tem4 = world4;

			tem1 = (byte) (tem1 & 0xc0);
			tem2 = (byte) (tem2 >> 2);
			tem2 = (byte) (tem2 & 0x30);
			tem3 = (byte) (tem3 >> 4);
			tem3 = (byte) (tem3 & 0x0c);
			tem4 = (byte) (tem4 >> 6);
			tem4 = (byte) (tem4 & 0x03);
			tempbytes[j + 3] = (byte) (tem1 + tem2 + tem3 + tem4);
			out1.write(tempbytes[j + 3]);
		}

		switch (exworld) {
		case 0:
			out1.close();
			break;
		case 1:
			out1.write(f1world[realbyte]);
			out1.close();
			break;
		case 2:
			out1.write(f1world[realbyte]);
			out1.write(f1world[realbyte + 1]);
			out1.close();
			break;
		case 3:
			out1.write(f1world[realbyte]);
			out1.write(f1world[realbyte + 1]);
			out1.write(f1world[realbyte + 2]);
			out1.close();
			break;
		default:
			break;
		}
		in1.close();
		in2.close();
		in3.close();
		in4.close();
		out1.close();
	}
	
	/**
	 * 上传文件
	 * @param filemap
	 * @param cloudmap
	 * @param preStr
	 * @return
	 */
	public static HashMap<String, List<CloudFile>> uploadToCloud(HashMap<Integer, String> filemap, HashMap<Integer, Integer> cloudmap, String preStr){
		int cloudCount = cloudmap.size();
		int copys = 0;
		switch(cloudCount){
		case 1:
		case 2:
			copys = 0;
			break;
		case 3:
		case 4:
			copys = 2;
			break;
		}
		HashMap<String, List<CloudFile>> map = new HashMap<String, List<CloudFile>>();
		Cloud cloud;
		/**
		 * i 备份数
		 * j 文件index
		 * k 云数
		 */
		for(int i = copys; i >= 0; i--){
			for(int j = 1; j <= 4; j++){
				for(int k = 1; k <= cloudCount; k++){
					if(((k + j - 1) % 4 - i) == 0){
						String filename = filemap.get(j);
						String filepath = preStr + filemap.get(j);
						int cloud_index = cloudmap.get(k);
						String id = "";
						String cloudName = "";
						switch(cloud_index){
						case 1://AliYun
							cloudName = "AliYun";
							try {
								cloud = new Aliyun();
								logger.info("upload to " + cloudName + " file: " + filename);
								id = cloud.upload(filepath, filename);
								logger.info("upload to " + cloudName + " file: " + filename + " success");
								if(id == null)
									return null;
							} catch (Exception e) {
								logger.info(filename + " to " + cloudName + "  failed");
								e.printStackTrace();
								return null;
							}
							break;
						case 2://GoogleDrive
							cloudName = "GoogleDrive";
							try {
								cloud = new GoogleDrive();
								logger.info("upload to " + cloudName + " file: " + filename);
								id = cloud.upload(filepath, filename);
								logger.info("upload to " + cloudName + " file: " + filename + "success");
								if(id == null)
									return null;
							} catch (Exception e) {
								logger.info(filename + " to " + cloudName + "  failed");
								e.printStackTrace();
								return null;
							}
							break;
						case 3://DropBox
							cloudName = "DropBox";
							try {
								cloud = new DropBox();
								logger.info("upload to " + cloudName + " file: " + filename);
								id = cloud.upload(filepath, filename);
								logger.info("upload to " + cloudName + " file: " + filename + "success");
								if(id == null)
									return null;
							} catch (Exception e) {
								logger.info(filename + " to " + cloudName + "  failed");
								e.printStackTrace();
								return null;
							}
							break;
						case 4://AmazonS3
							cloudName = "AmazonS3";
							try {
								cloud = new AmazonS3();
								logger.info("upload to " + cloudName + " file: " + filename);
								id = cloud.upload(filepath, filename);
								logger.info("upload to " + cloudName + " file: " + filename + "success");
								if(id == null)
									return null;
							} catch (IOException e) {
								logger.info(filename + " to " + cloudName + "  failed");
								e.printStackTrace();
								return null;
							}
							break;
						}
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						CloudFile cloudfile = new CloudFile(j, cloudName, filename, format.format(new Date()), id);
						List<CloudFile> list;
						if(map.get(filename) == null){
							list = new ArrayList<CloudFile>();
							map.put(filename, list);
						}
						else{
							list = map.get(filename);
						}
						list.add(cloudfile);
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 下载分片文件
	 * @param list
	 * @return 分片文件路径
	 */
	public static String dowloadFromCloud(List<CloudFile> list, String preStr){
		String path = null;
		Cloud cloud;
		for(CloudFile cloudFile:list){
			switch(cloudFile.getCloudname()){
			case "AliYun"://Ali
				try {
					cloud = new Aliyun();
					path = cloud.download(cloudFile.getId(), preStr);
				} catch (Exception e) {
					logger.info(cloudFile.getId() + " download from Aliyun failed...");
				}
				break;
			case "GoogleDrive"://google
				try {
					cloud = new GoogleDrive();
					path = cloud.download(cloudFile.getId(), preStr);
				} catch (Exception e) {
					logger.info(cloudFile.getId() + " download from GoogleDrive failed...");
				}
				break;
			case "DropBox"://brop
				try {
					cloud = new DropBox();
					path = cloud.download(cloudFile.getId(), preStr);
				} catch (Exception e) {
					logger.info(cloudFile.getId() + " download from DropBox failed...");
				}
				break;
			case "AmazonS3"://amazon
				try {
					cloud = new AmazonS3();
					path = cloud.download(cloudFile.getId(), preStr);
				} catch (Exception e) {
					logger.info(cloudFile.getId() + " download from AmazonS3 failed...");
				}
				break;
			}
			if(path != null)
				break;
		}
		return path;
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = new Date();
//		logger.info(format.format(date));
//		HashMap<Integer, List<String>> map = new HashMap<>();
//		List<String> list = new ArrayList<String>();
//		list.add("123123");
//		map.put(1, list);
//		System.out.println(map.get(1).size());
//		map.get(1).add("123121233");
//		System.out.println(map.get(1).size());
//		System.out.println(map.get(1));
		int copys = 2;
		for(int i = copys; i >= 0; i--){
			for(int j = 1; j <= 4; j++){
				for(int k = 1; k <= 1; k++){
					if(((k + j - 1) % 4 - i) == 0){
						System.out.println("file:" + j + " cloud:" +k);
					}
				}
			}
		}
		
		
		
//		System.out.println(UUID.randomUUID());
//		System.out.println(FileSecurity.class.getResource("/").getPath());
//		String path = "/Users/tanjie/work/upload/src.zip";
//		String pre = "/Users/tanjie/work/upload/tmp/";
//		String downPath = "/Users/tanjie/work/upload/tmpx/src.zip";
//		HashMap<Integer, String> map = FileSecurity.encrypt(path, pre);
//		logger.info("encrypty sucess...");
//		FileSecurity.decrypt(downPath, map, pre);
//		logger.info("decrypty sucess...");
	}

}
