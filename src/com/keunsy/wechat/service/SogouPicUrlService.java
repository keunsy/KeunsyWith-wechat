package com.keunsy.wechat.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sword.lang.HttpUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 图片爬虫业务 ，暂时只支持 搜狗图片
 */
public class SogouPicUrlService {

    private final static Logger log = Logger.getLogger(SogouPicUrlService.class);

    private final static String URL_PRE = "http://pic.sogou.com/pics?mood=0&picformat=0&mode=1&di=2&w=05009900&dr=1&_asf=pic.sogou.com&_ast=1451981901&start=48&reqType=ajax&tn=0&reqFrom=result&query=";

    private final static int LIMIT_COUNT = 9;

    public static List<String> getPicUrlList(String word, int limitCount) {

	List<String> picList = null;

	try {
	    String url = URL_PRE + URLEncoder.encode(word.trim(), "gbk");// 该网站要求gbk编码
	    String result = null;
	    JSONObject json = null;
	    // http请求
	    result = HttpUtils.get(url);

	    if (StringUtils.isNotBlank(result)) {
		picList = new ArrayList<String>();
		json = JSONObject.parseObject(result);// 转化json
		JSONArray itemsArray = json.getJSONArray("items");
		for (int i = 0, size = itemsArray.size(); i < size; i++) {
		    JSONObject items = (JSONObject) itemsArray.get(i);
		    String picUrl = items.getString("thumbUrl");
		    picList.add(picUrl);
		    // 数量限制
		    limitCount = limitCount == 0 ? LIMIT_COUNT : limitCount;
		    if (picList.size() == limitCount) {
			break;
		    }
		}
	    }
	    // for (String string : picList) {
	    // System.out.println(string);
	    // }
	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}

	return picList;
    }

    public static void main(String[] args) {
	SogouPicUrlService.getPicUrlList("非住", 1);
	// System.out.println(URLEncoder.encode("非住"));
	// try {
	// System.out.println(URLEncoder.encode("非住", "gbk"));
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
    }
}
