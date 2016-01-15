package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.select.Elements;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.BusService;
import com.keunsy.wechat.utils.EhcacheUtil;

/**
 *
 */
public class RealtimeBusDirProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(RealtimeBusDirProcess.class);

    private final String KEYWORD = "公交方向";

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;
	String busLine = null;
	// 查询
	try {
	    busLine = content.replaceFirst(KEYWORD, "").trim();
	    // 获取缓存
	    articleList = (List<ArticleResponse>) EhcacheUtil.get(EhcacheUtil.BUS_CACHE, busLine);
	    if (articleList != null && articleList.size() > 0) {
		return articleList;
	    }

	    // 获取返回元素
	    Elements results = BusService.getBusDirElements(busLine);

	    String dir1 = results.get(1).text();
	    String dir2 = results.get(2).text();
	    String title = "1.  " + dir1 + "\n" + "2.  " + dir2;
	    title += "\n\n" + function.getTitle();

	    articleList = new ArrayList<ArticleResponse>();
	    ArticleResponse article = new ArticleResponse();
	    article.setTitle(title);
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
	// 放入缓存
	if (StringUtils.isNotBlank(busLine)) {
	    EhcacheUtil.put(EhcacheUtil.BUS_CACHE, busLine, articleList);
	}

	return articleList;

    }

    public static void main(String[] args) {

	Function function = new Function();
	function.setKeyword("表情");
	new RealtimeBusDirProcess().excute(function, "公交方向621");

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
