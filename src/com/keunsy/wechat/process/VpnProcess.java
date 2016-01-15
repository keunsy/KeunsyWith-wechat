package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.service.ExtractService;
import com.keunsy.wechat.utils.EhcacheUtil;

/**
 * 豆瓣电影 可进行缓存，避免多次请求浪费资源
 *
 */
public class VpnProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(VpnProcess.class);

    private final String URL = "http://free.vpn.wwdhz.com/";
    private final String PWD_URL = "http://45.79.92.249/mm.txt";
    final String TAG_NAME = "container";
    private final String KEY = "vpn_cache_key";

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	QueryRule rule = null;
	List<ArticleResponse> articleList = null;
	// 查询
	try {
	    // 获取缓存
	    articleList = (List<ArticleResponse>) EhcacheUtil.get(EhcacheUtil.VPN_CACHE, KEY);
	    if (articleList != null && articleList.size() > 0) {
		return articleList;
	    }
	    articleList = new ArrayList<ArticleResponse>();
	    rule = new QueryRule();
	    rule.setUrl(URL);
	    rule.setResultTagName(TAG_NAME);
	    rule.setType(QueryRule.CLASS);
	    rule.setRequestMoethod(QueryRule.GET);
	    Elements results = ExtractService.extract(rule);
	    StringBuffer sBuffer = new StringBuffer();
	    // 获取标题
	    Element container = results.get(0);
	    String title = container.child(0).text();
	    sBuffer.append(title).append("\n");
	    // 获取内容
	    Elements pEles = container.getElementsByClass("txt").get(0).getElementsByTag("p");
	    int count = 0;
	    int limit = 3;
	    for (Element element : pEles) {
		String temp = null;
		Elements iframes = element.getElementsByTag("iframe");
		if (iframes != null && iframes.size() > 0) {
		    Connection conn = Jsoup.connect(PWD_URL);
		    Document doc = conn.userAgent("Mozilla").timeout(100000).get();
		    temp = "密码：" + doc.text();
		} else {
		    temp = element.text();
		}
		sBuffer.append("\n").append(temp);
		if (++count >= limit) {// 设置循环限制
		    break;
		}
	    }
	    sBuffer.append("\n连接方式：PPTP");
	    ArticleResponse article = new ArticleResponse();
	    article.setTitle(sBuffer.toString());
	    articleList.add(article);

	} catch (Exception e) {
	    e.printStackTrace();
	    // 格式较多，容易出现异常
	    log.error(null, e);
	}
	// 防止找不到
	if (articleList == null || articleList.size() == 0) {
	    articleList = new ArrayList<ArticleResponse>();
	    ArticleResponse article = new ArticleResponse();
	    article.setTitle(function.getWrong_msg());
	    articleList.add(article);
	} else {
	    // 放入缓存,不缓存错误的
	    EhcacheUtil.put(EhcacheUtil.VPN_CACHE, KEY, articleList);
	}

	return articleList;

    }

    public static void main(String[] args) {

	new VpnProcess().excute(null, null);
    }
}
