package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
public class DoubanMovieProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(DoubanMovieProcess.class);

    private final String URL_PRE = "http://movie.douban.com/nowplaying/beijing/";
    private final String TAG_NAME = "nowplaying";
    private final int LIMIT_COUNT = 8;
    private final String KEY = "douban_movie_cache_key";

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	QueryRule rule = null;
	List<ArticleResponse> articleList = null;
	// 查询
	try {
	    // 获取缓存
	    articleList = (List<ArticleResponse>) EhcacheUtil.get(EhcacheUtil.DOUBAN_MOVIE_CACHE, KEY);
	    if (articleList != null && articleList.size() > 0) {
		return articleList;
	    }
	    articleList = new ArrayList<ArticleResponse>();
	    rule = new QueryRule();
	    rule.setUrl(URL_PRE);
	    rule.setResultTagName(TAG_NAME);
	    rule.setType(QueryRule.ID);
	    rule.setRequestMoethod(QueryRule.GET);
	    Elements results = ExtractService.extract(rule);

	    ArticleResponse article = null;
	    // 解析
	    for (Element result : results) {
		Elements elements = result.getElementsByClass("list-item");
		for (Element element : elements) {
		    // 获取需要数据
		    String data_title = element.attr("data-title");
		    String data_score = element.attr("data-score");
		    String data_duration = element.attr("data-duration");
		    String data_actors = element.attr("data-actors");
		    String data_director = element.attr("data-director");
		    String picUrl = element.getElementsByTag("img").attr("src");
		    String url = element.getElementsByTag("img").parents().attr("href");
		    // 数据装载
		    String desc = "导演：" + data_director + " | 主演：" + data_actors + " | 片长：" + data_duration;
		    article = new ArticleResponse();
		    article.setTitle(data_title + "(" + (StringUtils.isNotBlank(data_score) ? data_score : "暂无评") + "分) \n" + desc);
		    article.setPicUrl(picUrl);
		    article.setUrl(url);
		    articleList.add(article);
		    // 条数限制
		    if (articleList.size() == LIMIT_COUNT) {
			break;
		    }
		}
	    }

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
	// 查看更多
	ArticleResponse article = new ArticleResponse();
	article.setTitle("点击查看更多电影！");
	article.setPicUrl("http://img3.douban.com/pview/event_poster/raw/public/c4e908e2dd6a0a0.jpg");
	article.setUrl("http://movie.douban.com/");
	articleList.add(article);
	// 查看余位
	article = new ArticleResponse();
	article.setTitle("点击查看影院座位！\n(默认影院为新影联华谊兄弟华彩店)");
	article.setPicUrl("http://i04.pictn.sogoucdn.com/6aa66f2ece50c9bc");
	article.setUrl("http://theater.mtime.com/China_Beijing_Chaoyang/2559/");
	articleList.add(article);
	// 放入缓存,不缓存错误的
	if (articleList.size() == 10) {
	    EhcacheUtil.put(EhcacheUtil.DOUBAN_MOVIE_CACHE, KEY, articleList);
	}

	return articleList;

    }

    public static void main(String[] args) {

	Function function = new Function();
	function.setKeyword("表情");
	new DoubanMovieProcess().excute(function, "表情哈哈");

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
