/**
 * 
 */
package cn.edu.bistu.FileSecurity.security.in;

import java.io.IOException;

/**
 * @author sunxinwei
 *
 */
public interface Cloud {
	public String upload(String filepath, String filename) throws IOException;
	public String download(String filename, String preStr) throws IOException;
}
