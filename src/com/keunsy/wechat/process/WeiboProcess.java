package com.keunsy.wechat.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.service.ExtractService;

/**
 * 微博 抓取 ，新浪微博 refer机制，暂不可以使用
 *
 */
public class WeiboProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(WeiboProcess.class);

    // private final String URL = "http://weibo.com/linyuner90?is_all=1";
    // private final String URL =
    // "http://weibo.com/bjshi?is_all=1&sudaref=passport.weibo.com";
    private final String URL = "http://weibo.cn/linyuner90";
    private final String TAG_NAME = "WB_frame_c";
    private final int LIMIT_COUNT = 9;

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	QueryRule rule = null;
	List<ArticleResponse> articleList = null;

	// 查询
	try {
	    String url = function.getUrl();// 该网站要求gbk编码
	    rule = new QueryRule();
	    rule.setUrl(URL);
	    rule.setResultTagName(TAG_NAME);
	    rule.setType(QueryRule.CLASS);
	    rule.setRequestMoethod(QueryRule.POST);
	    Elements results = ExtractService.extract(rule);

	    articleList = new ArrayList<ArticleResponse>();
	    ArticleResponse article = null;
	    // 解析
	    for (Element result : results) {
		Elements elements = result.getElementsByClass("WB_detail");
		for (Element element : elements) {
		    try {
			// 获取需要数据
			String title = element.child(0).text();
			String picUrl = element.child(1).getElementsByTag("img").attr("src");
			if (StringUtils.isNotBlank(title)) {
			    // 去除所有标签
			    title = Jsoup.clean(title, Whitelist.none());
			}
			// 数据装载
			article = new ArticleResponse();
			article.setTitle(title);
			article.setPicUrl(picUrl);
			article.setUrl(picUrl);
			articleList.add(article);
			// 条数限制
			if (articleList.size() == LIMIT_COUNT) {
			    break;
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	    log.error(null, e);
	}

	return articleList;

    }

    public static void main(String[] args) {

	Function function = new Function();
	function.setKeyword("");
	new WeiboProcess().excute(function, "表情哈哈");

	// try {
	// Document doc =
	// Jsoup.connect("http://www.cnblogs.com/txw1958/p/weixin-97-news.html").get();
	// doc.toString();
	// System.out.println("fe");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
    }
}
