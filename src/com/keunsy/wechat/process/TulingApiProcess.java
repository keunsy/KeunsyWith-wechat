package com.keunsy.wechat.process;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.sword.wechat4j.response.ArticleResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.SogouPicUrlService;

/**
 * 调用图灵机器人api接口，获取智能回复内容
 * 
 *
 */
public class TulingApiProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(TulingApiProcess.class);

    /** 此处为图灵api接口 */
    private final static String APIURL = "http://www.tuling123.com/openapi/api?key=9712a5206ab13e94c34c7088b3486910&info=";
    private final static String CODE = "utf-8";
    private final static String WEATHER_KEY = "天气";
    private final int LIMIT_COUNT = 6;

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	String param = "";
	try {
	    param = APIURL + URLEncoder.encode(content, CODE);
	} catch (UnsupportedEncodingException e1) {
	    e1.printStackTrace();
	} // 将参数转为url编码

	/** 发送httpget请求 */
	HttpGet request = new HttpGet(param);
	String result = null;
	try {
	    HttpResponse response = HttpClients.createDefault().execute(request);
	    if (response.getStatusLine().getStatusCode() == 200) {
		result = EntityUtils.toString(response.getEntity());
	    }
	} catch (ClientProtocolException e) {
	    log.error(null, e);
	    e.printStackTrace();
	} catch (IOException e) {
	    log.error(null, e);
	    e.printStackTrace();
	}

	List<ArticleResponse> articleList = null;

	try {
	    JSONObject json = JSONObject.parseObject(result);

	    log.info(json.toJSONString());
	    // 以code=100000为例，参考图灵机器人api文档
	    int code = json.getIntValue("code");
	    JSONObject tempJson;
	    ArticleResponse article = new ArticleResponse();
	    JSONArray array = json.getJSONArray("list");
	    articleList = new ArrayList<ArticleResponse>();

	    switch (code) {
	    case 100000:// 文本
		/*
		 * 图灵天气格式不满足要求，需另写，现无较可靠的接口，待定 （图灵天气格式：北京:01/06 周三,-6-3° 1° 晴
		 * 北风3-4级;01/07 周四,-7-2° 晴 北风3-4级;01/08 周五,-9-2° 晴 北风3-4级;01/09
		 * 周六,-7-2° 晴 无持续风微风;）
		 */
		String text = json.getString("text");
		article.setTitle(text);
		try {
		    if (content.contains(WEATHER_KEY)) {// 部分天气关键词返回的非天气数据，此时异常采用原有方式即可
			String location = text.substring(0, text.indexOf(":"));
			String dataStr = text.substring(text.indexOf(":") + 1);
			dataStr = dataStr.replaceAll("\\s", " | ").replaceAll(";", "\n\n").replaceAll(",", "  :  ");
			article.setTitle(location + WEATHER_KEY);
			article.setUrl("http://www.weather.com.cn/weather1d/101010100.shtml");// 默认地址
			article.setDescription(dataStr);
		    }
		} catch (Exception e) {
		}
		articleList.add(article);
		break;
	    case 200000:// 链接类
		article.setTitle(json.getString("text") + "（点我跳转，注意流量）");
		article.setUrl(json.getString("url"));
		articleList.add(article);
		break;
	    case 302000:// 新闻
		// text = json.getString("text");
		if (array != null) {
		    for (int i = 0; i < array.size(); i++) {
			tempJson = (JSONObject) array.get(i);
			article = new ArticleResponse();
			String title = tempJson.getString("article");
			article.setTitle(title);
			// 爬虫获取图片
			List<String> picList = SogouPicUrlService.getPicUrlList(title, 1);
			if (picList != null && picList.size() != 0) {
			    article.setPicUrl(picList.get(0));
			}
			// article.setDescription(tempJson.getString("article"));
			article.setUrl(tempJson.getString("detailurl"));
			articleList.add(article);
			if (articleList.size() == LIMIT_COUNT) {
			    break;
			}
		    }
		}
		break;
	    case 308000:// 菜谱
		if (array != null) {
		    for (int i = 0; i < array.size(); i++) {
			tempJson = (JSONObject) array.get(i);
			article = new ArticleResponse();
			String info = tempJson.getString("name") + "\n" + tempJson.getString("info");
			article.setTitle(info);
			article.setPicUrl(tempJson.getString("icon"));
			article.setUrl(tempJson.getString("detailurl"));
			articleList.add(article);
			if (articleList.size() == LIMIT_COUNT) {
			    break;
			}
		    }
		}
		break;
	    default:
	    }

	} catch (JSONException e) {
	    log.error(null, e);
	    e.printStackTrace();

	}

	return articleList;
    }

    public static void main(String[] args) {

	TulingApiProcess process = new TulingApiProcess();
	process.excute(null, "北京天气");
	// getTulingResult(new KeunsyWechat(null), "番茄炒蛋怎么做");
	// getTulingResult("北京天气");
	// getTulingResult(new KeunsyWechat(null), "北京到拉萨的火车");
	// getTulingResult(new KeunsyWechat(null), "笑话");
	// getTulingResult(new KeunsyWechat(null), "北京到上海的飞机");
	// getTulingResult(new KeunsyWechat(null), "新闻");
	// getTulingResult(new KeunsyWechat(null), "笑话");
    }
}
