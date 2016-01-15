package com.keunsy.wechat.service;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import com.keunsy.wechat.dao.BasicDAO;
import com.keunsy.wechat.entity.WeiboMonitor;
import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.service.ExtractService;
import com.keunsy.wechat.utils.JDBCUtil;

/**
 * 微博
 */
public class WeiboService {

    private final static Logger log = Logger.getLogger(WeiboService.class);

    private static WeiboService weiboService = new WeiboService();

    public static WeiboService getInstance() {
	return weiboService;
    }

    private final static String URL_PRE = "http://weibo.cn/";
    private final static String TAG_NAME = "c";

    private final static String SELECT_SQL_PRE = "select * from weibo_monitor";

    /**
     * 获取元素
     */
    public Elements getWeiBoElements(WeiboMonitor weiboMonitor) {

	QueryRule rule = null;
	Elements results = null;

	try {
	    String url = URL_PRE + weiboMonitor.getWeibo_addr();
	    rule = new QueryRule();
	    rule.setUrl(url);
	    rule.setResultTagName(TAG_NAME);
	    rule.setType(QueryRule.ID_MATCH);
	    rule.setRequestMoethod(QueryRule.GET);
	    results = ExtractService.extract(rule);
	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}

	return results;
    }

    public QueryRule getWeiboQueryRule(String weibo_addr) {

	String url = URL_PRE + weibo_addr;
	QueryRule rule = new QueryRule();
	rule.setUrl(url);
	rule.setResultTagName(TAG_NAME);
	rule.setType(QueryRule.ID_MATCH);
	rule.setRequestMoethod(QueryRule.GET);

	return rule;
    }

    /**
     * 查询
     * 
     * @param type
     * @return
     */
    public List<WeiboMonitor> getWeiboMonitorList() {
	Connection conn = JDBCUtil.getConnection();
	return BasicDAO.queryList(conn, SELECT_SQL_PRE, WeiboMonitor.class);
    }

    public static void main(String[] args) {

	WeiboMonitor weiBoMonitor = new WeiboMonitor();
	weiBoMonitor.setWeibo_addr("linyuner90");
	WeiboService.getInstance().getWeiBoElements(weiBoMonitor);
    }
}
