/**
 * Created by sunxinwei on 9/1/15.
 */
var tabCount = 1;

$(document).ready(function(){
    var tabs = $("#tabs").tabs();
    $("#sortfield").buttonset();
    $("#sorttype").buttonset();
    var progressbar = $("#progressbar");
    var progressLabel = $(".progress-label");
    
    $("#progressbar").progressbar({
        value: false,
        change: function() {
            progressLabel.text(progressbar.progressbar("value") + "%");
        },
        complete: function() {
            progressLabel.text("Complete!");
        }
    });
    
    $('#fileupload').fileupload({
        dataType:"json",
        url:"rest/filetool/uploadfile",
        //formData: {example: 'test'}
        submit: function(e, data){
        	progressbar.progressbar("value", false);
        	$("#progressbar_div").show();
        	$("#errorup").hide();
        	var c1 = $("#cloud1").prop("checked");
        	var c2 = $("#cloud2").prop("checked");
        	var c3 = $("#cloud3").prop("checked");
        	var c4 = $("#cloud4").prop("checked");
        	data.formData = {
        			clouds: c1 + "#" + c2 + "#" + c3 + "#" + c4
        	};
        	//$(this).fileupload("submit", data);
        	//return false;
        },
        progressall: function(e, data){
        	$("#nowsize").html(data.loaded);
        	$("#totalsize").html(data.total);
            var progress = parseInt(data.loaded / data.total * 100, 10);
            progressbar.progressbar("value", progress);
        },
        always: function(e, data){
        	var param = "param=" + data._response.jqXHR.responseText;
        	$("#treelist").html("Loading...");
        	progressbar.progressbar("value", false);
        	progressLabel.text("Loading...");
        	$.post("http://localhost:8080/FSS/rest/filetool/uploadToCloud", param, function(data){
        		if(data == "error"){
                	$("#errorup").show();
        		}
                var progress = parseInt(100 / 100 * 100, 10);
                progressbar.progressbar("value", progress);
    		    filelist();
        	});
        }
    });
    
//    $("#hahaha").click(function(){
//    	$("#lwelcome").remove();
//    	$("#welcome").remove();
//    });

    tabs.delegate("span.ui-icon-close", "click", function(){
        var panelId = $(this).closest("li").remove().attr("aria-controls");
        $("#" + panelId).remove();
        tabCount--;
        tabs.tabs("refresh");
    });

    tabs.bind("keyup", function(event) {
        if (event.altKey && event.keyCode === $.ui.keyCode.BACKSPACE) {
            var panelId = tabs.find( ".ui-tabs-active" ).remove().attr("aria-controls");
            $("#" + panelId).remove();
            tabCount--;
            tabs.tabs("refresh");
        }
    });

    var filelist = function(){
    	$.post("http://localhost:8080/FSS/rest/fileinfo/sysfile", "", function(json){
    		var html = "";
    		if(json.length == 0){
    			html = "No files!";
    		}else{
        		$.each(json, function(index, data){
        			html += "<li class=\"file ext_" + data.type + "\"><a class=\"a_file\" href=\"javascript:void(0);\" rel=\"" + data.filename + "\">" + data.filename + "<span class=\"uploaddate\">" + data.uploadDate + "</span></a></li>";
        		});
    		}
    		$("#treelist").html(html);
    		
    		$(".a_file").click(function(){
    			var filename = $(this).attr("rel");
    			var param = "filename=" + filename;
    			$.post("http://localhost:8080/FSS/rest/fileinfo/filedetail", param, function(detail){
    				//alert(JSON.stringify(detail));
    				var query = false;
    				$("#tabs ul li").each(function(index){
    					var filename = $(this).children().first().text();
    					if(filename == detail.file.filename){
    						var select = $(this).attr("id");
    						if(select != "lwelcome"){
    							select = select.substring(1, select.length);
    							tabs.tabs("option", "active", select);
    							query == true;
    							return false;
    						}
    					}
    				});
    				
    				if(!query){
        				var id = "tab-" + tabCount;
        				var li = "<li id=\"l" + id + "\"><a href=\"#" + id + "\">" + detail.file.filename + "</a><span class=\"ui-icon ui-icon-close\" role=\"presentation\">Remove Tab</span></li>";
        				var tabValue = "<div id=\"" + id + "\"><div class=\"file_content\"><div class=\"view_file\"></div><table class=\"file_info\"><tr><th>file name:</th><td>" + detail.file.filename + "</td></tr> <tr><th>file type:</th><td>" + detail.file.type + "</td></tr><tr><th>upload date:</th><td>" + detail.file.uploadDate + "</td></tr><tr><td colspan=\"2\"><hr></td></tr><tr>"
        				var len = 0;
        				$.each(detail.splits, function(key, value){
        					len++;
        				});
        				tabValue += "<th rowspan=\"" + len + "\">storage cloud:</th>";
        				var i = 0;
        				$.each(detail.splits, function(key, value){
        					if(i == 0){
            					tabValue += "<td>" + key + "</td></tr>";
        					}else{
        						tabValue += "<tr><td>" + key + "</td></tr>";
        					}
        					i++;
        				});
        				tabValue += "</table><hr><div class=\"download_div\"><input type=\"hidden\" id=\"" + id + "_input\" name=\"fileid\" value=\"" + detail.file._id +"\"/><button id=\"" + id + "_download\" class=\"download\">Download</button></div></div></div>";
        				tabs.find(".ui-tabs-nav").append(li);
        				tabs.append(tabValue);
        				tabs.tabs("refresh");
        				$("#" + id + "_download").button();
        				tabCount++;
        				//选中当前tab
        				var index = $("#tabs ul").index($("#" + id));
        				tabs.tabs("option", "active", index);
        				
        				//download
        				$(".download").click(function(){
        					var tabid = $(this).attr("id");
        					tabid = tabid.substring(0, tabid.length-9);
        					var file_id = $("#" + tabid + "_input").val();
        					//var param = "fileid=" + file_id;
        					
        					$("#fileid").val(file_id);
        					$("#downloadForm").submit();
        				});
    				}
    			}, "json");
    		});
    	}, "json");
    };

    $(".logout").click(function(){
        //log out
    });

    $("#uploadfilebutton").click(function(){
        $(this).slideUp();
        $("#file_upload").slideDown();
    });

    filelist();
});