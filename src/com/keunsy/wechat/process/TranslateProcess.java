package com.keunsy.wechat.process;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.sword.lang.HttpUtils;
import org.sword.wechat4j.response.ArticleResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.utils.MD5Util;

/**
 * 翻译处理
 *
 */
public class TranslateProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(TranslateProcess.class);

    private final String URL_PRE = "http://api.fanyi.baidu.com/api/trans/vip/translate?";
    private final String FROM = "auto";// 语言来源 自动
    private final String APPID = "20151228000008330";
    private final String PWD = "CfuNNkckUgrMujdOKjUg";
    private final int TRY_TIMES = 1;

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;

	try {

	    StringBuffer url = new StringBuffer(URL_PRE);
	    String q = content.replaceFirst(function.getKeyword(), "").trim();
	    String to = getToLauguage(q);
	    String salt = UUID.randomUUID().toString();
	    String sign = MD5Util.MD5(APPID + q + salt + PWD).toLowerCase();

	    url.append("q=").append(URLEncoder.encode(q, "utf-8"));
	    url.append("&").append("from=").append(FROM); // 翻译源语言
	    url.append("&").append("to=").append(to); // 译文语言
	    url.append("&").append("appid=").append(APPID); // APPID
	    url.append("&").append("salt=").append(salt); // 随机数
	    url.append("&").append("sign=").append(sign); // 签名
	    // http请求
	    String result = null;
	    JSONObject json = null;
	    int times = 0;
	    // 是否返回了错误，如果错误再次请求
	    while (times <= TRY_TIMES) {
		result = HttpUtils.get(url.toString());
		json = JSONObject.parseObject(result);
		if (json.getString("error_code") == null) {
		    break;
		} else {
		    times++;
		}
	    }
	    if (times <= TRY_TIMES) {// 表示成功
		articleList = new ArrayList<ArticleResponse>();
		JSONArray trans_result_array = json.getJSONArray("trans_result");
		JSONObject trans_result = (JSONObject) trans_result_array.get(0);
		String dst = trans_result.getString("dst");
		ArticleResponse article = new ArticleResponse();
		article.setTitle(dst);
		articleList.add(article);
	    }

	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}

	return articleList;

    }

    /**
     * 获取 目标语言
     * 
     * @param str
     * @return
     */
    public String getToLauguage(String str) {
	String to = "en";
	if (!str.matches("(.*)[\\u4e00-\\u9fbf]+(.*)")) {// 不包含中文则返回中文编码
	    to = "zh";
	}
	return to;
    }

    public static void main(String[] args) {
	String url = "http://api.map.baidu.com/location/ip?ak=E4805d16520de693a3fe707cdc962045&ip=202.198.16.3";
	String result = HttpUtils.get(url);
	System.out.println(result);

	// new TranslateProcess().excute(null, "is");
	// new TranslateProcess().excute(null, "한국어");
	// new TranslateProcess().excute(null, "翻译翻译");
	// new TranslateProcess().excute(null, "عربي/عربى‎");

    }
}
