/**
 * Created by sunxinwei on 8/31/15.
 */
$(document).ready(function(){
	var init = function(user){
		var param = "id=" + user._id;
		$.post("http://localhost:8080/FSS/fileinfo/getfileslist", param, function(data){
			var html = "";
	        if(data.filelist.length == 0){
	            html = "No files!";
	        }
	        else{
	            $.each(data.filelist, function(index, data){
	                html += "<li class=\"file ext_" + data.type + "\"><a class=\"a_file\" href=\"javascript:void(0);\" rel=\"" + data.filename + "\">" + data.filename + "<span class=\"uploaddate\">" + data.uploadDate + "</span></a></li>";
	            });
	        }
	        $("#treelist").html(html);

	        $(".a_file").click(function(){
	            //alert($(this).attr("rel"));
	            var filename = $(this).attr("rel");
	            $.getJSON("js/test.json", function(data){
	                if(data.filelist.length == 0){
	                    //html += "<div id=\"welcome\">Error!</div>";
	                }
	                else{
	                    $.each(data.filelist, function(index, data){
	                        if(filename == data.filename){
	                            var id = "tab-" + tabCount;
	                            var li = "<li><a href=\"#" + id + "\">" + data.filename + "</a><span class=\"ui-icon ui-icon-close\" role=\"presentation\">Remove Tab</span></li>";
	                            var tabValue = "<div id=\"" + id + "\"><div class=\"file_content\"><div class=\"view_file\"></div><table class=\"file_info\"><tr><th>file name:</th><td>" + data.filename + "</td></tr> <tr><th>file type:</th><td>" + data.type + "</td></tr><tr><th>upload date:</th><td>" + data.uploadDate + "</td></tr><tr><td colspan=\"2\"><hr></td></tr><tr><th rowspan=\"3\">storage cloud:</th><td>" + data.split1.cloud + "</td></tr><tr><td>" + data.split2.cloud + "</td></tr><tr><td>" + data.split3.cloud + "</td></tr></table><hr><div class=\"download_div\"><input type=\"hidden\" id=\"" + id + "_input\" name=\"fileid\" value=\"" + data.id +"\"/><button id=\"" + id + "_download\" class=\"download\">Download</button></div></div></div>";
	                            tabs.find( ".ui-tabs-nav" ).append( li );
	                            tabs.append(tabValue);
	                            tabs.tabs("refresh");
	                            $("#" + id + "_download").button();
	                            tabCount++;
	                            //选中当前tab
	                            var index = $("#tabs ul").index($("#" + id));
	                            tabs.tabs("option", "active", index);
	                            return false;
	                        }
	                    });
	                }
	            });
	        });
		}, "json");
	};
	
    var remmemberme = $.cookie("remmemberme");
    if(remmemberme != undefined){
        if(remmemberme == "false"){
            $.post("http://localhost:8080/FSS/rest/userinfo/sessioninfo", "", function(data) {
                if(data == undefined){
                    $.removeCookie("remmemberme");
                    $.removeCookie("username");
                    $.removeCookie("password");
                    window.location = "index.html";
                }
                else{
                	$("#nickname").html(data.nickname);
                	$("#userid").val(data._id);
                }
            }, "json");
        }
        else if(remmemberme == "true"){
            var username = $.cookie("username");
            var password = $.cookie("password");
            var paramData = "username=" + username + "&password=" + password;
            $.post("http://localhost:8080/FSS/rest/userinfo/login", paramData, function(data){
                if(data == undefined){
                    $.removeCookie("remmemberme");
                    $.removeCookie("username");
                    $.removeCookie("password");
                    window.location = "index.html";
                }
                else{
                	$("#nickname").html(data.nickname);
                	$("#userid").val(data._id);
                }
            }, "json");
        }
    }
    else {
        window.location = "index.html";
    }
});