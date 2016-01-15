package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sword.lang.HttpUtils;
import org.sword.wechat4j.response.ArticleResponse;

import com.alibaba.fastjson.JSONObject;
import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.BaiduDevService;
import com.keunsy.wechat.service.BusService;

/**
 */
public class RealtimeBusProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(RealtimeBusProcess.class);

    private final String URL_PRE = "http://www.bjbus.com/home/ajax_search_bus_stop.php?act=busTime";

    private final String KEYWORD = "实时公交";

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;
	String url = URL_PRE;
	// 查询
	try {
	    String info = content.replaceFirst(KEYWORD, "").trim();
	    String info_array[] = info.split("\\s+");
	    String selBLine = info_array[0].trim();// 线路
	    String dir = "1";// 默认1
	    if (info_array.length > 1) {
		dir = info_array[1].trim();// 方向
	    }
	    String selBStop = "null";
	    if (info_array.length == 3) {
		selBStop = info_array[2].trim();// 站点
	    }
	    // 获取返回元素
	    Elements busDirEles = BusService.getBusDirElements(selBLine);

	    String selBDir = busDirEles.get(Integer.parseInt(dir)).val();// 方向值
	    url = BaiduDevService.appendParam(url, "selBLine", selBLine);
	    url = BaiduDevService.appendParam(url, "selBDir", selBDir);
	    url = BaiduDevService.appendParam(url, "selBStop", selBStop);

	    // http请求
	    String result = HttpUtils.get(url);

	    JSONObject json = JSONObject.parseObject(result);
	    String html = json.getString("html");
	    // 构建dom
	    Document doc = Jsoup.parse(html);
	    StringBuffer sBuffer = new StringBuffer();

	    // 公交信息
	    String line = doc.getElementById("lh").text();
	    String dir2 = doc.getElementById("lm").text();
	    sBuffer.append("路线：").append(line).append("(").append(dir2).append(")");
	    // 头信息（如果选定了站点）
	    if (StringUtils.isNotBlank(selBStop) && !selBStop.equals("null")) {
		Element article = doc.getElementsByTag("article").get(0);
		Elements pEles = article.getElementsByTag("p");
		sBuffer.append("\n信息：").append("(");
		for (Element element : pEles) {
		    sBuffer.append(element.text()).append(".");
		}
		sBuffer.append(")");
	    }
	    sBuffer.append("\n\n");
	    // 实时公交详情
	    Elements li = doc.getElementsByTag("li");
	    String split = "-";
	    // 拼接实时公交
	    for (Element element : li) {
		boolean flag = false;// 辨别空元素
		Element i = element.getElementsByTag("i").get(0);
		if (StringUtils.isNotBlank(i.attr("class"))) {// 有class值 则为实时公交点
		    sBuffer.append("\ue01e");
		    flag = true;
		}
		Elements spans = element.getElementsByTag("span");
		if (spans != null && spans.size() > 0) {
		    sBuffer.append("[").append(spans.get(0).text()).append("]");
		    flag = true;
		}
		if (flag) {
		    sBuffer.append(split);
		}
	    }
	    String title = sBuffer.substring(0, sBuffer.lastIndexOf(split));
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

	return articleList;

    }

    public static void main(String[] args) {

	new RealtimeBusProcess().excute(null, "621 1 45");

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
