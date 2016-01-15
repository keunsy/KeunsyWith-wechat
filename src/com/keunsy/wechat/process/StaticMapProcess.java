package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.BaiduDevService;

/**
 * 天气处理
 *
 */
public class StaticMapProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(StaticMapProcess.class);

    private final String URL_PRE = "http://api.map.baidu.com/staticimage?";
    private final String KEYWORD = "地图";

    private final String BIG_SIZE = "&width=600&height=600";
    private final String SMALL_SIZE = "&width=240&height=100";// 微信限制

    // 方法级别表
    private static Map<String, Integer> levelMap = new HashMap<String, Integer>();
    static {
	levelMap.put("国", 4);
	levelMap.put("省", 8);
	levelMap.put("市", 12);
	levelMap.put("街", 18);
    }

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;
	ArticleResponse article = null;
	String url = URL_PRE;
	int zoom = 18;// 默认街道级别

	try {
	    String zoom_center = content.replaceFirst(KEYWORD, "").trim();
	    String center = zoom_center;// 默认不需要级别
	    String level = zoom_center.substring(0, 1).trim();
	    if (levelMap.get(level) != null) {// 填写了级别参数
		zoom = levelMap.get(level);
		center = zoom_center.substring(1).trim();
	    } else {
		level = "街";
	    }
	    url = BaiduDevService.appendAK(url);
	    url = BaiduDevService.appendCenter(url, center);
	    url = BaiduDevService.appendZoom(url, zoom);
	    url = BaiduDevService.appendMarkers(url, center);

	    articleList = new ArrayList<ArticleResponse>();
	    article = new ArticleResponse();
	    article.setTitle(center + "(" + level + ")地图:");
	    article.setPicUrl(BaiduDevService.appendParam(url, null, SMALL_SIZE));// 微信对于图文消息图片有限制
	    article.setUrl(BaiduDevService.appendParam(url, null, BIG_SIZE));
	    articleList.add(article);

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

	new StaticMapProcess().excute(null, "地图市望京soho");

	// new TranslateProcess().excute(null, "is");
	// new TranslateProcess().excute(null, "한국어");
	// new TranslateProcess().excute(null, "翻译翻译");
	// new TranslateProcess().excute(null, "عربي/عربى‎");

    }
}
