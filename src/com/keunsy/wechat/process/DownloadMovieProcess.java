package com.keunsy.wechat.process;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.service.ExtractService;
import com.keunsy.wechat.utils.EhcacheUtil;

/**
 *
 */
public class DownloadMovieProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(DownloadMovieProcess.class);

    private final String URL_PRE = "http://www.id97.com/videos/search/name/";
    private final String HOST = "http://www.id97.com";
    private final String TAG_NAME = "result-item";
    private final String WRONG_MSG = "未找到相关影片下载资源，请更改关键词后再试试~";

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	QueryRule rule = null;
	List<ArticleResponse> articleList = null;
	ArticleResponse article = null;
	String q = "";

	// 查询
	try {
	    q = content.replaceFirst("电影", "").trim();
	    // 获取缓存
	    articleList = (List<ArticleResponse>) EhcacheUtil.get(EhcacheUtil.DOWNLOAD_MOVIE_CACHE, q);
	    if (articleList != null && articleList.size() > 0) {
		return articleList;
	    }

	    String url = URL_PRE + URLEncoder.encode(q, "utf-8");
	    // 初始解析获取页面链接
	    rule = new QueryRule();
	    rule.setUrl(url);
	    rule.setResultTagName(TAG_NAME);
	    rule.setType(QueryRule.CLASS);
	    rule.setRequestMoethod(QueryRule.GET);
	    Elements results1 = ExtractService.extract(rule);
	    Element element1 = results1.get(0);
	    // 二次解析
	    Element tempEle = element1.getElementsByTag("img").get(0);
	    String url2 = HOST + tempEle.parent().attr("href");
	    rule = new QueryRule();
	    rule.setUrl(url2);
	    rule.setResultTagName("resource-list");
	    rule.setType(QueryRule.CLASS);
	    rule.setRequestMoethod(QueryRule.GET);
	    Elements results2 = ExtractService.extract(rule);

	    StringBuffer sBuffer = new StringBuffer();
	    // 在线观看的情况
	    Elements results3 = results2.get(0).getElementsByClass("list-group-item");
	    if (results3 != null && results3.size() > 0) {
		Elements results31 = results3.get(0).getElementsByTag("a");
		for (int i = 0; i < results31.size(); i++) {
		    sBuffer.append("\n\n在线观看" + (i + 1) + ":");
		    String href = results31.get(i).attr("href");
		    if (!href.startsWith("http")) {
			sBuffer.append(HOST);
		    }
		    sBuffer.append(href);
		}
	    }
	    // 百度网盘磁力链的情况
	    Elements results4 = results2.get(0).getElementsByClass("text-break");
	    // 解析
	    if (results4 != null && results4.size() > 0) {
		for (Element element : results4) {
		    String downloadType = "磁力链：";
		    String psd = "";
		    // 获取需要数据
		    Elements strongs = element.child(0).getElementsByTag("strong");
		    // 有strong则为 百度网盘，否则为磁力链
		    if (strongs != null && strongs.size() > 0) {
			downloadType = "百度网盘：";
			psd = " | 密码：" + strongs.get(0).text().trim();
		    }
		    String href = element.child(0).child(0).attr("href");
		    sBuffer.append("\n\n").append(downloadType).append(href).append(psd);
		}
	    }
	    // 获取标题
	    String movieName = tempEle.attr("alt");
	    movieName = movieName != null ? movieName : "";
	    String version = tempEle.parents().get(0).text();
	    version = version != null ? version : "";
	    // 参数设置
	    articleList = new ArrayList<ArticleResponse>();
	    article = new ArticleResponse();
	    article.setTitle(movieName + "(" + version + ")" + sBuffer.toString());
	    // article.setUrl(url2);
	    articleList.add(article);

	} catch (Exception e) {
	    e.printStackTrace();
	    log.error(null, e);
	}
	// 防止找不到
	if (articleList == null || articleList.size() == 0) {
	    articleList = new ArrayList<ArticleResponse>();
	    article = new ArticleResponse();
	    article.setTitle(WRONG_MSG);
	    articleList.add(article);
	} else {
	    article.setTitle(article.getTitle() + "\n\n注：磁力链可下载或通过网盘在线观看\n注：如出现异常情况，请联系Keunsy获取其他下载方式");
	}
	// 放入缓存
	EhcacheUtil.put(EhcacheUtil.DOWNLOAD_MOVIE_CACHE, q, articleList);

	return articleList;

    }

    public static void main(String[] args) {

	Function function = new Function();
	function.setKeyword("表情");
	new DownloadMovieProcess().excute(function, "电影老炮儿");

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
