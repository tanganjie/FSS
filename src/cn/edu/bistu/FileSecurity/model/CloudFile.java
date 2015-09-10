/**
 * 
 */
package cn.edu.bistu.FileSecurity.model;

/**
 * @author sunxinwei
 *
 */
public class CloudFile {
	private int index;
	private String cloudname;
	private String filename;
	private String uploadDate;
	private String id;

	/**
	 * @param index
	 * @param cloudname
	 * @param filename
	 * @param uploadDate
	 * @param id
	 */
	public CloudFile(int index, String cloudname, String filename, String uploadDate, String id) {
		super();
		this.index = index;
		this.cloudname = cloudname;
		this.filename = filename;
		this.uploadDate = uploadDate;
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CloudFile [cloudname=" + cloudname + ", filename=" + filename + ", uploadDate=" + uploadDate + "]";
	}
	
	/**
	 * default
	 */
	public CloudFile() {
	}

	/**
	 * @return the cloudname
	 */
	public String getCloudname() {
		return cloudname;
	}

	/**
	 * @param cloudname the cloudname to set
	 */
	public void setCloudname(String cloudname) {
		this.cloudname = cloudname;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the uploadDate
	 */
	public String getUploadDate() {
		return uploadDate;
	}

	/**
	 * @param uploadDate the uploadDate to set
	 */
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
