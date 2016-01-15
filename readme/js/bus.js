
var selBLine = "";
var selBDir = "";
var selBStop = "";
var selLine = "";
$.getUrlParam = function(c) {
	var b = new RegExp("(^|&)" + c + "=([^&]*)(&|$)"),
	d = window.location.search.substr(1).match(b);
	return null != d ? unescape(d[2]) : null
};
$.getParam = function(f, i) {
	var g = new RegExp("(^|&|\\?|#)" + f + "=([^&#]*)(&|\x24|#)", ""),
	d = i,
	c;
	if (!d) {
		try {
			d = decodeURIComponent(location.href)
		} catch(h) {
			d = location.href
		}
	}
	c = d.match(g);
	if (c) {
		return c[2]
	}
	return ""
};

$.isEmpty = function(str){
	if(str != null && str != "" && typeof(str == "undefined") && str != "null"){
		return true
	}else{
		return false
	}
};
$('#searchNB').click(function(){
	selLine = $("#selLine").val();
    ajax_getLineTime();
});


function ajax_getLineTime(){
    var htmlobj = $.ajax({
        url: "ajax_search_bus_stop.php",
        type: "GET",
        async: true,
        data: "act=nightBus&selLine="+selLine,
        dataType: "json",
        success: function(data, textStatus){
            if(data.err){
                alert(data.err);
            }else{
                $("#selLine").find("option[value='"+selLine+"']").attr("selected",true);
                $(".timetable").html(data.html);
            }
        }
    });
}

$("ul.sub_nav_left li a").click(function(){
    var obj1 = $("ul.sub_nav_left li:eq(0) a:eq(0)");
    var obj2 = $("ul.sub_nav_left li:eq(1) a:eq(0)");
    if($(this).html() == obj1.html()){
        $("#busStop").show();
        $("#nightBus").hide();
    }else if($(this).html() == obj2.html()){
        $("#busStop").hide();
        $("#nightBus").show();
    }
});
var timeout;
$('#searchBS').click(function(){
    var htmlobj = $.ajax({
        url: "ajax_search_bus_stop.php",
        type: "GET",
        async: true,
        data: "act=busTime&selBLine="+selBLine+"&selBDir="+selBDir+"&selBStop="+selBStop,
        dataType: "json",
        success: function(data, textStatus){
            if(data.err != undefined){
               alert(data.err);
            }else{
                $("#station_info").html(data.html);
                $("#cc_stop ul").css("width", data.w);
                $("#station").show();
                var container = $('#cc_stop'),scrollTo = $("#"+data.seq);
                container.scrollLeft(
                    scrollTo.offset().left - container.offset().left + container.scrollLeft() - 450
                );
                container.animate({
                    scrollTop: scrollTo.offset().left - container.offset().left + container.scrollLeft()
                });
                clearInterval(timeout);
                timeout = setInterval(getBusGPS, 15000); // 1s鎵ц涓€娆tnCount
                getLTurl();
            }
        }
    });
	
});

$("#selBLine").change(function(){
	selBLine = encodeURI($('#selBLine').val());
    var htmlobj = $.ajax({
        url: "ajax_search_bus_stop.php",
        type: "GET",
        async: true,
        data: "act=getLineDirOption&selBLine="+selBLine,
        dataType: "text",
        success: function(data, textStatus){
            $("#selBDir").html(data);
        }
    });
});

$("#selBDir").change(function(){
	selBDir = $('#selBDir').val();
    var htmlobj = $.ajax({
        url: "ajax_search_bus_stop.php",
        type: "GET",
        async: true,
        data: "act=getDirStationOption&selBLine="+selBLine+"&selBDir="+selBDir,
        dataType: "text",
        success: function(data, textStatus){
            $("#selBStop").html(data);
        }
    });
});

$("#selBStop").change(function(){
	selBStop = $('#selBStop').val();
});	

$("#busImgBg_close").click(function(){
    $("#busImgBg").hide();
    $("#busImgBg_").hide();
    $("#busImgBg_close").hide();
});

function getBusGPS(){
	
    var htmlobj = $.ajax({
        url: "ajax_search_bus_stop.php",
        type: "GET",
        async: true,
        data: "act=busTime&selBLine="+selBLine+"&selBDir="+selBDir+"&selBStop="+selBStop,
        dataType: "json",
        success: function(data, textStatus){
            if(data.err != undefined){
               alert(data.err);
            }else{
                $("#station_info").html(data.html);
                $("#cc_stop ul").css("width", data.w);
                $("#station").show();
                var container = $('#cc_stop'),scrollTo = $("#"+data.seq);
                container.scrollLeft(
                    scrollTo.offset().left - container.offset().left + container.scrollLeft() - 450
                );
                // Or you can animate the scrolling:
                container.animate({
                    scrollTop: scrollTo.offset().left - container.offset().left + container.scrollLeft()
                });
                getLTurl();
            }
        }
    });
    
}

function showBusImg(uuid){
    $("#busImgBg_").attr("src", "../RTBus/data/images/bus/"+uuid+".jpg");
    $("#busImgBg").show();
    $("#busImgBg_").show();
    $("#busImgBg_close").show();
}

function showBusTime(linename){
	$("#selLine option").each(function(){
		if($(this).text().indexOf(decodeURI(linename)) != -1){	
			$("#selLine").find("option[text='"+$(this).text()+"']").attr("selected",true);
			selLine = $(this).val();
			ajax_getLineTime();
		}
	});
}


selLine = $.getUrlParam("sLl");
if($.isEmpty(selLine)) 
	ajax_getLineTime();

selBLine = encodeURI($.getParam("sBl"));
selBDir = $.getUrlParam("sBd");
selBStop = $.getUrlParam("sBs");
if($.isEmpty(selBLine) && $.isEmpty(selBDir) && $.isEmpty(selBStop)) 
	$('#searchBS').trigger('click');

var selLineN = encodeURI($.getParam("sLn"));
if($.isEmpty(selLineN)) 
	showBusTime(selLineN);

function getLTurl(){
	var uSec = $.getUrlParam("uSec");
	var uSub = $.getUrlParam("uSub");
	var lh = $("#lh").html().substring(0, ($("#lh").html().length-1));
	var lm = $("#lm").html();
	var ln = lh+"("+lm+")";
	$("#linetime").attr("href", "fun_rtbus_nighttime.php?uSec="+uSec+"&uSub="+uSub+"&sLn="+encodeURI(ln));
	$("#transmap").attr("href", "../RTBus/data/images/bus/"+selBDir+".jpg");
}


