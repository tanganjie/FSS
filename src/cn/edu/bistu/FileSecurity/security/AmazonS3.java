/**
 * 
 */
package cn.edu.bistu.FileSecurity.security;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.TransferManager;

import cn.edu.bistu.FileSecurity.security.in.Cloud;

/**
 * @author sunxinwei
 *
 */
public class AmazonS3 implements Cloud {
	static Logger logger = Logger.getLogger(AmazonS3.class.getName());
	private static final String bucketName = "sxwfsss";
	private static final String accessKey = "AKIAI2UN2FJNAK7YQMTQ";
	private static final String secretKey = "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";
	private AmazonS3Client s3;

	/**
	 * 
	 */
	public AmazonS3() {
		AWSCredentials credential = new BasicAWSCredentials(accessKey, secretKey);
		this.s3 = new AmazonS3Client(credential);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
        //s3.createBucket(bucketName);
	}

	/* (non-Javadoc)
	 * @see cn.edu.bistu.FileSecurity.security.in.Cloud#upload(java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(String filepath, String filename) throws IOException {
		File file = new File(filepath);
		PutObjectResult result = s3.putObject(bucketName, filename, file);
		//logger.info(result.getETag());
		return filename;
	}

	/* (non-Javadoc)
	 * @see cn.edu.bistu.FileSecurity.security.in.Cloud#download(java.lang.String, java.lang.String)
	 */
	@Override
	public String download(String filename, String preStr) throws IOException {
		String filepath = preStr + UUID.randomUUID();
		ObjectMetadata metadata = s3.getObject(new GetObjectRequest(bucketName, filename), new File(filepath));
		//logger.info(metadata.getETag());
		return filepath;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		AmazonS3 s3 = new AmazonS3();
		//s3.upload("/Users/tanjie/work/upload/uploadtest.txt", "uploadtest.txt");
		s3.download("uploadtest.txt", "/Users/tanjie/work/upload/tmp/");
	}

}
