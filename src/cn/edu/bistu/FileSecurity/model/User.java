/**
 * 
 */
package cn.edu.bistu.FileSecurity.model;

import org.bson.Document;

/**
 * @author sunxinwei
 *
 */
public class User {
	private String _id;
	private String username;
	private String password;
	private String nickname;
	private String email;
	
	public User() {
		
	}
	
	/**
	 * @param _id
	 * @param username
	 * @param password
	 * @param nickname
	 * @param email
	 */
	public User(String _id, String username, String password, String nickname,
			String email) {
		super();
		this._id = _id;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.email = email;
	}
	
	public Document parseMongoDoc(){
		return new Document().append("_id", _id)
				.append("username", username)
				.append("password", password)
				.append("nickname", nickname)
				.append("email", email);
	}
	public void parseUser(Document doc){
		this._id = doc.getString("_id");
		this.username = doc.getString("username");
		this.password = doc.getString("password");
		this.nickname = doc.getString("nickname");
		this.email = doc.getString("email");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [id=" + _id + ", username=" + username + ", password="
				+ password + ", nickname=" + nickname + ", email=" + email
				+ "]";
	}
	
	/**
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}
	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
