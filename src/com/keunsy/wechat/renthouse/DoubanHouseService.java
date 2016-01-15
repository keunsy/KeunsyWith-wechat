package com.keunsy.wechat.renthouse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.keunsy.wechat.mail.SendMailService;
import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.service.ExtractService;

public class DoubanHouseService {

    private static String QUERY_1 = "【望京，小组】";
    private static String QUERY_2 = "【望京西园四区】";
    private static String today_time = "2015-08-21";
    private static boolean isRun = true;

    private static Map<String, String> paramMap = new HashMap<String, String>();

    private static final Logger logger = Logger.getLogger("rentHouseLog");

    static {
	paramMap.put("cat", "1013");
	paramMap.put("q", "望京");
	paramMap.put("sort", "time");
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	today_time = df.format(new Date());
	logger.info("DoubanSpider Init Success!");
    }

    public static synchronized void getHouseInfoByGroup(String queryFlag) {

	QueryRule rule = new QueryRule();
	rule.setUrl("http://www.douban.com/group/search");
	rule.setResultTagName("topics");
	rule.setType(QueryRule.CLASS);
	rule.setRequestMoethod(QueryRule.POST);
	rule.setParamMap(paramMap);

	int count = 1;
	String queryWord = queryFlag;// 不转换会有异常
	Map<String, String> infoMap = new HashMap<String, String>();

	while (isRun) {
	    // 数量过多进行清楚
	    if (infoMap.size() == 500) {
		infoMap = new HashMap<String, String>();
		logger.info("Clear Query Map！");
	    }

	    logger.info(queryWord + "第" + (count++) + "次查询...");
	    try {
		// 查询
		Elements results = ExtractService.extract(rule);

		String content = null;
		// 解析
		for (Element result : results) {
		    content = "";
		    Elements elements = result.getElementsByTag("tr");
		    for (Element element : elements) {
			try {
			    String title = element.child(0).children().text();
			    String href = element.child(0).children().attr("href");
			    String time = element.child(1).attr("title");
			    String replyInfo = element.child(2).children().text();
			    if (time.compareTo(today_time) >= 0) {
				String temp = title + "(" + href + ")" + "\r\n" + time + "  |  " + replyInfo + "\r\n" + "==========================================================" + "\r\n";

				if (infoMap.get(time) == null) {
				    infoMap.put(time, temp);
				    content += temp;
				}

			    }
			} catch (Exception e) {
			    logger.info("Error Occur!");
			    logger.error("Error Occur!", e);
			    e.printStackTrace();
			}
		    }
		}

		if (content != null && !"".equals(content)) {
		    logger.info(queryWord + "\r\n" + content);
		    SendMailService.sendMail("豆瓣租房推送" + queryWord, content);
		} else {
		    logger.info(today_time + " Have No New Info!");
		}

		Thread.sleep(300000);
	    } catch (InterruptedException e) {
		logger.info("Error Occur!");
		logger.error("Error Occur!", e);
		e.printStackTrace();
		try {
		    Thread.sleep(100000);
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}
	    } catch (Exception e) {
		logger.info("Error Occur!");
		logger.error("Error Occur!", e);
		e.printStackTrace();
		try {
		    Thread.sleep(100000);
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}
	    }
	}
    }

    /*
     * 望京，小组
     */
    public static void getHouseInfoByGroup1(Map<String, String> map) {

	logger.info(QUERY_1 + " Start!");
	paramMap.put("q", "望京");
	paramMap.put("group", "35417");
	if (map != null) {
	    paramMap = map;
	}
	getHouseInfoByGroup(QUERY_1);

    }

    /*
     * 望京西园四区
     */
    public static void getHouseInfoByGroup2(Map<String, String> map) {

	logger.info(QUERY_2 + " Start!");

	paramMap.put("q", "望京西园四区");
	if (map != null) {
	    paramMap = map;
	}
	getHouseInfoByGroup(QUERY_2);
    }

    /**
     * 停止
     */
    public static void stop() {
	isRun = false;
    }
}
