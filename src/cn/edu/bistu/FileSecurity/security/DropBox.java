/**
 * 
 */
package cn.edu.bistu.FileSecurity.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

import cn.edu.bistu.FileSecurity.security.in.Cloud;

/**
 * @author sunxinwei
 *
 */
public class DropBox implements Cloud {
	static Logger logger = Logger.getLogger(DropBox.class.getName());
	private static final String APP_KEY = "8jrik6txf1shkni";
	private static final String APP_SECRET = "ygmwv500v7ngin6";
	private static final String token = "MiYSeIHiXycAAAAAAAAAEWFL8zRl6slYll-ibDkhCpSvTFkwQvSSJJo0hB_A2AUX";
	private static DbxClient client;

	/**
	 * @throws DbxException 
	 * 
	 */
	public DropBox() throws DbxException {
		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
        client = new DbxClient(config, token);
        System.out.println("Linked account: " + client.getAccountInfo().displayName);
	}

	/* (non-Javadoc)
	 * @see cn.edu.bistu.FileSecurity.security.in.Cloud#upload(java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(String filepath, String filename) throws IOException {
		File inputFile = new File(filepath);
		InputStream inputStream = new FileInputStream(inputFile);
		try {
			DbxEntry.File uploadedFile;
			try {
				uploadedFile = client.uploadFile("/" + filename, DbxWriteMode.add(), inputFile.length(), inputStream);
				logger.info(uploadedFile.toString());
				return uploadedFile.name;
			} catch (DbxException e) {
				return null;
			}
		} finally {
			inputStream.close();
		}
	}

	/* (non-Javadoc)
	 * @see cn.edu.bistu.FileSecurity.security.in.Cloud#download(java.lang.String, java.lang.String)
	 */
	@Override
	public String download(String filename, String preStr) throws IOException {
		String filepath = preStr + UUID.randomUUID();
		OutputStream outputStream = new FileOutputStream(filepath);
		try {
			DbxEntry.File downloadedFile;
			try {
				downloadedFile = client.getFile("/" + filename, null, outputStream);
				System.out.println("Metadata: " + downloadedFile.toString());
				return filepath;
			} catch (DbxException e) {
				return null;
			}
		} finally {
			outputStream.close();
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws DbxException 
	 */
	public static void main(String[] args) throws IOException, DbxException {
		DropBox dropBox = new DropBox();
		dropBox.upload("/Users/tanjie/work/upload/uploadtest.txt", "uploadtest.txt");
		//dropBox.download("uploadtest.txt", "/Users/tanjie/work/upload/tmp/");
	}

}
