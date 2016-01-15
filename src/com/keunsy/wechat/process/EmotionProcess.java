package com.keunsy.wechat.process;

import java.net.URLEncoder;
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
import com.keunsy.wechat.utils.EhcacheUtil;

/**
 * 普通问答处理
 *
 */
public class EmotionProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(EmotionProcess.class);

    private final String URL_PRE = "http://md.itlun.cn/plus/search.php?pagesize=20&kwtype=1&searchtype=titlekeyword&q=";
    private final String TAG_NAME = "pic";
    private final int LIMIT_COUNT = 9;

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	QueryRule rule = null;
	List<ArticleResponse> articleList = null;
	String q = "";

	// 查询
	try {
	    q = content.replaceFirst(function.getKeyword(), "").trim();
	    // 获取缓存
	    articleList = (List<ArticleResponse>) EhcacheUtil.get(EhcacheUtil.EMOTION_CACHE, q);
	    if (articleList != null && articleList.size() > 0) {
		return articleList;
	    }
	    String url = URL_PRE + URLEncoder.encode(q, "gbk");// 该网站要求gbk编码

	    rule = new QueryRule();
	    rule.setUrl(url);
	    rule.setResultTagName(TAG_NAME);
	    rule.setType(QueryRule.CLASS);
	    rule.setRequestMoethod(QueryRule.POST);
	    Elements results = ExtractService.extract(rule);

	    articleList = new ArrayList<ArticleResponse>();
	    ArticleResponse article = null;
	    // 解析
	    for (Element result : results) {
		Elements elements = result.getElementsByTag("a");
		for (Element element : elements) {
		    // 获取需要数据
		    String picUrl = element.child(0).attr("src");
		    String title = element.attr("title");
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
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    log.error(null, e);
	}
	// 防止找不到
	if (articleList == null || articleList.size() == 0) {
	    articleList = new ArrayList<ArticleResponse>();
	    ArticleResponse article = new ArticleResponse();
	    article.setTitle(function.getWrong_msg());
	    articleList.add(article);
	}
	// 放入缓存
	EhcacheUtil.put(EhcacheUtil.EMOTION_CACHE, q, articleList);

	return articleList;

    }

    public static void main(String[] args) {

	// Function function = new Function();
	// function.setKeyword("表情");
	// new EmotionProcess().excute(function, "表情哈哈");

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
