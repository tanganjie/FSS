/**
 * 
 */
package cn.edu.bistu.FileSecurity.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import cn.edu.bistu.FileSecurity.security.in.Cloud;

/**
 * @author sunxinwei
 *
 */
public class GoogleDrive implements Cloud {
	static Logger logger = Logger.getLogger(GoogleDrive.class.getName());

	private static final String appname = "fss";
	private static final String client_id = "136410947228-ljg6cber1j7agssvfikah5b32hlu8aih.apps.googleusercontent.com";
	private static final String client_secret = "xEe0tKsnxXvX6XLSMsImfivL";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/googledrive");
	private static HttpTransport transport;
	private static Drive drive;

	/**
	 * init
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public GoogleDrive() throws GeneralSecurityException, IOException {
		transport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
		        new InputStreamReader(GoogleDrive.class.getResourceAsStream("/client_secret.json")));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				transport, jsonFactory, clientSecrets,
		        Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
		        .build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		drive = new Drive.Builder(
				transport, jsonFactory, credential)
                .setApplicationName(appname)
                .build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.bistu.FileSecurity.security.in.Cloud#upload(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String upload(String filepath, String filename) throws IOException {
		File fileMetadata = new File();
		java.io.File UPLOAD_FILE = new java.io.File(filepath);
	    fileMetadata.setTitle(filename);
	    logger.info(fileMetadata.getId());

	    FileContent mediaContent = new FileContent("", UPLOAD_FILE);
	    Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
	    MediaHttpUploader uploader = insert.getMediaHttpUploader();
	    uploader.setDirectUploadEnabled(true);
	    uploader.setProgressListener(new FileUploadProgressListener());
	    File result = insert.execute();
	    logger.info(result.getId());
		return result.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.bistu.FileSecurity.security.in.Cloud#download(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String download(String filename, String preStr) throws IOException {
		String filepath = preStr + UUID.randomUUID();
	    OutputStream out = new FileOutputStream(new java.io.File(filepath));
	    
	    Get request = drive.files().get(filename);
	    request.getMediaHttpDownloader().setDirectDownloadEnabled(true);
	    request.getMediaHttpDownloader().setProgressListener(new FileDownloadProgressListener());
	    request.executeMediaAndDownloadTo(out);
	    logger.info("google download " + filename + " ok...");
		return filepath;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public static void main(String[] args) throws GeneralSecurityException, IOException {
		GoogleDrive google = new GoogleDrive();
		String id = google.upload("/Users/tanjie/work/upload/uploadtest.txt", "uploa");
		google.download(id, "/Users/tanjie/work/upload/");
	}

	class FileUploadProgressListener implements MediaHttpUploaderProgressListener {
		@Override
		public void progressChanged(MediaHttpUploader uploader) throws IOException {
			switch (uploader.getUploadState()) {
			case INITIATION_STARTED:
				logger.info("Upload Initiation has started.");
				break;
			case INITIATION_COMPLETE:
				logger.info("Upload Initiation is Complete.");
				break;
			case MEDIA_IN_PROGRESS:
				logger.info("Upload is In Progress: " + NumberFormat.getPercentInstance().format(uploader.getProgress()));
				break;
			case MEDIA_COMPLETE:
				logger.info("Upload is Complete!");
				break;
			}
		}
	}
	
	class FileDownloadProgressListener implements MediaHttpDownloaderProgressListener {
		@Override
		public void progressChanged(MediaHttpDownloader downloader) {
			switch (downloader.getDownloadState()) {
			case MEDIA_IN_PROGRESS:
				logger.info("Download is in progress: " + downloader.getProgress());
				break;
			case MEDIA_COMPLETE:
				logger.info("Download is Complete!");
				break;
			}
		}
	}
}


