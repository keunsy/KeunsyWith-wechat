package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sword.lang.HttpUtils;
import org.sword.wechat4j.response.ArticleResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.BaiduDevService;
import com.keunsy.wechat.utils.EhcacheUtil;

/**
 * 天气处理
 *
 */
public class WeatherProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(WeatherProcess.class);

    private final String URL_PRE = "http://api.map.baidu.com/telematics/v3/weather?output=json";
    private final String KEYWORD = "天气";

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;
	String url = URL_PRE;
	ArticleResponse article = null;
	String location = "";
	String spilt = "  |  ";

	try {
	    location = content.replaceFirst(KEYWORD, "").trim();
	    // 获取缓存
	    articleList = (List<ArticleResponse>) EhcacheUtil.get(EhcacheUtil.WEATHER_CACHE, location);
	    if (articleList != null && articleList.size() > 0) {
		return articleList;
	    }
	    url = BaiduDevService.appendAK(url);
	    url = BaiduDevService.appendParam(url, "location", location);
	    // http请求
	    String result = HttpUtils.get(url);
	    JSONObject json = JSONObject.parseObject(result);
	    if (json.getInteger("error") != 0) {// 错误的情况
		throw new RuntimeException();
	    }
	    // 解析
	    articleList = new ArrayList<ArticleResponse>();
	    JSONArray results_array = json.getJSONArray("results");
	    JSONObject results = (JSONObject) results_array.get(0);
	    String pm25 = results.getString("pm25");
	    if (StringUtils.isNotBlank(pm25)) {
		pm25 = spilt + "空气质量:" + pm25;
	    }
	    String currentCity = results.getString("currentCity");

	    // 首栏
	    article = new ArticleResponse();
	    article.setTitle(currentCity + "天气预报");
	    articleList.add(article);
	    // 建议情况
	    JSONArray index_array = results.getJSONArray("index");
	    JSONObject index = (JSONObject) index_array.get(0);
	    String des = index.getString("des");
	    // 第二栏
	    article = new ArticleResponse();
	    article.setTitle("今日建议：" + des);
	    articleList.add(article);
	    // 天气预报
	    JSONArray weather_data = results.getJSONArray("weather_data");

	    // 循环
	    for (int i = 0, size = weather_data.size(); i < size; i++) {

		JSONObject weatherJson = (JSONObject) weather_data.get(i);
		String date = weatherJson.getString("date");// 日期
		String dayPictureUrl = weatherJson.getString("dayPictureUrl");// 图片地址
		String weather = weatherJson.getString("weather");// 天气状况
		String wind = weatherJson.getString("wind");// 风
		String temperature = weatherJson.getString("temperature");// 温度
		if (i == 0) {
		    date = "今日  " + date + pm25;
		}
		article = new ArticleResponse();
		article.setTitle(date + "\n" + weather + spilt + wind + spilt + temperature);
		article.setPicUrl(dayPictureUrl);
		articleList.add(article);
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
	// 放入缓存
	EhcacheUtil.put(EhcacheUtil.WEATHER_CACHE, location, articleList);

	return articleList;

    }

    public static void main(String[] args) {
	new WeatherProcess().excute(null, "天气朝阳区");

    }
}
