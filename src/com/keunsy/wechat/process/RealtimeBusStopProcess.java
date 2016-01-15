package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.BusService;
import com.keunsy.wechat.utils.EhcacheUtil;

/**
 *
 */
public class RealtimeBusStopProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(RealtimeBusStopProcess.class);

    private final String KEYWORD = "公交站点";
    private final String CACHE_KEY_MID = "DIR";// 防止出现 路线为 10 方向为1 相加为101
					       // 导致错误的情况

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;
	String selBLine = null;
	String dir = "1";// 默认1
	// 查询
	try {
	    String info = content.replaceFirst(KEYWORD, "").trim();
	    String info_array[] = info.split("\\s+");
	    selBLine = info_array[0].trim();// 线路

	    if (info_array.length > 1) {
		dir = info_array[1].trim();// 方向
	    }
	    // 获取缓存
	    articleList = (List<ArticleResponse>) EhcacheUtil.get(EhcacheUtil.BUS_CACHE, selBLine + CACHE_KEY_MID + dir);
	    if (articleList != null && articleList.size() > 0) {
		return articleList;
	    }

	    // 获取返回元素
	    Elements busDirEles = BusService.getBusDirElements(selBLine);
	    String selBDir = busDirEles.get(Integer.parseInt(dir)).val();// 方向值
	    // 获取返回元素
	    Elements results = BusService.getDirStationElements(selBLine, selBDir);

	    StringBuffer sBuffer = new StringBuffer();
	    sBuffer.append(function.getTitle()).append("\n");
	    for (Element element : results) {
		String value = element.val();
		if (StringUtils.isNotBlank(value)) {
		    sBuffer.append("\n").append(value).append(".").append(element.text());
		}
	    }

	    articleList = new ArrayList<ArticleResponse>();
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
	}
	// 放入缓存
	if (StringUtils.isNotBlank(selBLine)) {
	    EhcacheUtil.put(EhcacheUtil.BUS_CACHE, selBLine + CACHE_KEY_MID + dir, articleList);
	}

	return articleList;

    }

    public static void main(String[] args) {

	Function function = new Function();
	function.setKeyword("表情");
	new RealtimeBusStopProcess().excute(function, "公交站点621");

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
