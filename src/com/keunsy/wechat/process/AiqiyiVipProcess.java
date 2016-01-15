package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

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
 * 豆瓣电影 可进行缓存，避免多次请求浪费资源
 *
 */
public class AiqiyiVipProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(AiqiyiVipProcess.class);

    private final String URL_PRE = "http://www.wljx.net/forum-36-1.html";
    private final String URL_MAIN = "http://www.wljx.net/";
    final String TAG_NAME = "^normalthread_.*";
    private final String KEY = "video_vip_cache_key";

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	QueryRule rule = null;
	List<ArticleResponse> articleList = null;
	// 查询
	try {
	    // 获取缓存
	    articleList = (List<ArticleResponse>) EhcacheUtil.get(EhcacheUtil.VIDEO_VIP_CACHE, KEY);
	    if (articleList != null && articleList.size() > 0) {
		return articleList;
	    }
	    articleList = new ArrayList<ArticleResponse>();
	    rule = new QueryRule();
	    rule.setUrl(URL_PRE);
	    rule.setResultTagName(TAG_NAME);
	    rule.setType(QueryRule.ID_MATCH);
	    rule.setRequestMoethod(QueryRule.GET);
	    Elements results = ExtractService.extract(rule);
	    // 获取第一级url
	    Element tbody = results.get(0);
	    Element icn = tbody.getElementsByClass("icn").get(0);
	    String url2 = icn.child(0).attr("href");
	    url2 = URL_MAIN + url2;

	    rule = new QueryRule();
	    rule.setUrl(url2);
	    rule.setResultTagName("t_fsz");
	    rule.setType(QueryRule.CLASS);
	    rule.setRequestMoethod(QueryRule.GET);
	    Elements results2 = ExtractService.extract(rule);
	    Element t_fsz = results2.get(0);
	    Element tf = t_fsz.getElementsByClass("t_f").get(0);
	    String title = Jsoup.clean(tf.html(), Whitelist.none());

	    ArticleResponse article = new ArticleResponse();
	    article.setTitle(title.replaceAll("\\s", "\n"));
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
	}
	// 放入缓存,不缓存错误的
	if (articleList.size() == 10) {
	    EhcacheUtil.put(EhcacheUtil.VIDEO_VIP_CACHE, KEY, articleList);
	}

	return articleList;

    }

    public static void main(String[] args) {

	new AiqiyiVipProcess().excute(null, null);
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
