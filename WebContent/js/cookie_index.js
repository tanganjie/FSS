/**
 * Created by sunxinwei on 8/31/15.
 */
$(document).ready(function() {
	var remmemberme = $.cookie("remmemberme");
	if (remmemberme != undefined) {
		if (remmemberme == "false") {
			$.removeCookie("remmemberme");
			$.removeCookie("username");
			$.removeCookie("password");
		}
		else {
			var username = $.cookie("username");
			var password = $.cookie("password");
			var paramData = "username=" + username + "&password=" + password;
		    $.post("http://localhost:8080/FSS/rest/userinfo/login", paramData, function(data){
		    	if(data != undefined){
	    			window.location = "system_index.html";
		    	}else{
		    		$.removeCookie("remmemberme");
		    		$.removeCookie("username");
		    		$.removeCookie("password");
		    	}
		    }, "json");
		}
	}
});