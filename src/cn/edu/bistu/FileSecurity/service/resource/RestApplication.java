/**
 * 
 */
package cn.edu.bistu.FileSecurity.service.resource;

import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * @author sunxinwei
 *
 */
public class RestApplication extends ResourceConfig {

	/**
	 * 载入json支持
	 */
	public RestApplication() {
		// TODO Auto-generated constructor stub//服务类所在的包路径  
	     packages("cn.edu.bistu.FileSecurity.service");  
	     //注册JSON转换器  
	     register(JacksonJsonProvider.class);  
	}
}
