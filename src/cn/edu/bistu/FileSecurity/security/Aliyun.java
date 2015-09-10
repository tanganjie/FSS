/**
 * 
 */
package cn.edu.bistu.FileSecurity.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

import cn.edu.bistu.FileSecurity.security.in.Cloud;

/**
 * @author sunxinwei
 *
 */
public class Aliyun implements Cloud{
	static Logger logger = Logger.getLogger(Aliyun.class.getName());
	
    private static final String ACCESS_ID = "kAc8Ez9TpcB1J3Sr";
    private static final String ACCESS_KEY = "Z4PHZC0NQEadkoFda17j6opaJ61uRL";
    private static final String OSS_ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
    private static OSSClient client;
    private static String bucketName = "fss";
	
    /**
     * 初始化client
     */
    public Aliyun(){
    	client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
    }
    
    /**
     * 上传文件
     * @param filepath
     * @param filename
     * @return
     * @throws IOException
     */
    public String upload(String filepath, String filename) throws IOException{
    	File file = new File(filepath);
		InputStream is = null;
    	try {
			is = new FileInputStream(file);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(file.length());
			PutObjectResult result = client.putObject(bucketName, filename, is, meta);
			logger.info(result.getETag());
			return filename;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally{
			if(is != null)
				is.close();
		}
    	return null;
    }
    
    /**
     * 下载文件
     * @param filename
     * @return path
     */
    public String download(String filename, String preStr) throws IOException{
    	String path = preStr + UUID.randomUUID().toString();
    	File file = new File(path);

        ObjectMetadata meta = client.getObject(new GetObjectRequest(bucketName, filename), file);
    	logger.info(meta.toString());
    	return path;
    }
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String path = "/Users/tanjie/work/upload/uploadtest.txt";
		Aliyun yun = new Aliyun();
		//yun.upload(path, "uploadtest");
		yun.download("63a5f1b3-0ab0-44c3-83fe-f74ca46a8186", "/Users/tanjie/Downloads/");
	}

}
