package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sword.lang.HttpUtils;
import org.sword.wechat4j.response.ArticleResponse;

import com.alibaba.fastjson.JSONObject;
import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.BaiduDevService;
import com.keunsy.wechat.service.BaiduGeocodingService;

/**
 * 实景图
 *
 */
public class ScenePicProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(ScenePicProcess.class);

    private final String URL_PRE = "http://api.map.baidu.com/panorama/v2?";
    private final String KEYWORD = "实景";

    // 默认尺寸400X300

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;
	ArticleResponse article = null;
	String url = URL_PRE;

	try {
	    String address_city = content.replaceFirst(KEYWORD, "").trim();
	    String address = address_city;
	    String city = null;
	    int fov = 360;

	    // 拆分地址
	    String[] array = address_city.split("\\s+");
	    address = array[0].trim();
	    if (array.length > 1) {
		city = array[1].trim();
	    }
	    if (array.length == 3) {// 度数是可选的
		try {
		    int temp = Integer.parseInt(array[2].trim());
		    if (temp <= 180 || temp >= 360) {// 360度以内
			fov = temp;
		    }
		} catch (Exception e) {
		}
	    }
	    // 获取经纬度
	    String location = BaiduGeocodingService.getGeocoding(address, city);
	    // location = "116.33912,39.99303";
	    if (StringUtils.isNotBlank(location)) {
		url = BaiduDevService.appendAK(url);
		url = BaiduDevService.appendParam(url, "location", location);
		url = BaiduDevService.appendParam(url, "fov", String.valueOf(fov));
		// 请求验证地点是否存在 当返回的是正确的图片
		String result = HttpUtils.get(url.toString());
		try {
		    JSONObject.parseObject(result);// 返回正确的话为图片编码，此时不能转为json格式数据；存在错误情况则跳过
		} catch (Exception e) {
		    articleList = new ArrayList<ArticleResponse>();
		    article = new ArticleResponse();
		    article.setTitle(address + "|实景图:");
		    article.setPicUrl(url);//
		    article.setUrl(url);
		    articleList.add(article);
		}

	    }

	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}

	// 防止找不到
	if (articleList == null || articleList.size() == 0) {
	    articleList = new ArrayList<ArticleResponse>();
	    article = new ArticleResponse();
	    article.setTitle(function.getWrong_msg());
	    articleList.add(article);
	}

	return articleList;

    }

    public static void main(String[] args) {

	new ScenePicProcess().excute(null, "实景融科望京中心B座  ");

    }
}
