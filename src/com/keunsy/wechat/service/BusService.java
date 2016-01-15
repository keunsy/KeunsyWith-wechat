package com.keunsy.wechat.service;

import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.service.ExtractService;
import com.keunsy.wechat.utils.EhcacheUtil;

/**
 */
public class BusService {

    private final static Logger log = Logger.getLogger(BusService.class);
    // 方向
    private final static String SEARCH_BUS_DIR_PRE = "http://www.bjbus.com/home/ajax_search_bus_stop.php?act=getLineDirOption&selBLine=";
    private final static String SEARCH_BUS_DIR_TAG_NAME = "option";
    // 站点
    private final static String SEARCH_BUS_STOP_PRE = "http://www.bjbus.com/home/ajax_search_bus_stop.php?act=getDirStationOption";
    private final static String SEARCH_BUS_STOP_TAG_NAME = "option";

    // key前缀,防止与process重复
    private final static String BUS_SERVICE_CACHE_KEY_PRE = "BUS_SERVICE_CACHE_KEY";

    /**
     * 获取公交方向
     * 
     * @param busLine
     * @return
     */
    public static Elements getBusDirElements(String busLine) {

	QueryRule rule = null;
	Elements results = null;
	try {
	    // 获取缓存
	    results = (Elements) EhcacheUtil.get(EhcacheUtil.BUS_CACHE, BUS_SERVICE_CACHE_KEY_PRE + busLine);
	    if (results != null && results.size() > 0) {
		return results;
	    }

	    String url = SEARCH_BUS_DIR_PRE + busLine;
	    rule = new QueryRule();
	    rule.setUrl(url);
	    rule.setResultTagName(SEARCH_BUS_DIR_TAG_NAME);
	    rule.setType(QueryRule.SELECTION);
	    rule.setRequestMoethod(QueryRule.GET);
	    results = ExtractService.extract(rule);

	    // 放入缓存
	    EhcacheUtil.put(EhcacheUtil.BUS_CACHE, BUS_SERVICE_CACHE_KEY_PRE + busLine, results);
	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}

	return results;
    }

    /**
     * 获取某个公交方向下的站点
     * 
     * @param busLine
     * @return
     */
    public static Elements getDirStationElements(String selBLine, String selBDir) {

	QueryRule rule = null;
	Elements results = null;
	String url = SEARCH_BUS_STOP_PRE;
	try {
	    // 获取缓存
	    results = (Elements) EhcacheUtil.get(EhcacheUtil.BUS_CACHE, BUS_SERVICE_CACHE_KEY_PRE + selBLine + selBDir);
	    if (results != null && results.size() > 0) {
		return results;
	    }

	    url = BaiduDevService.appendParam(url, "selBLine", selBLine);
	    url = BaiduDevService.appendParam(url, "selBDir", selBDir);
	    rule = new QueryRule();
	    rule.setUrl(url);
	    rule.setResultTagName(SEARCH_BUS_STOP_TAG_NAME);
	    rule.setType(QueryRule.SELECTION);
	    rule.setRequestMoethod(QueryRule.GET);
	    results = ExtractService.extract(rule);
	    // 放入缓存
	    EhcacheUtil.put(EhcacheUtil.BUS_CACHE, BUS_SERVICE_CACHE_KEY_PRE + selBLine + selBDir, results);
	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}

	return results;
    }

    public static void main(String[] args) {
	String selBLine = "621";
	String dir = "1";
	Elements busDirEles = BusService.getBusDirElements(selBLine);
	String selBDir = busDirEles.get(Integer.parseInt(dir)).val();// 方向值
	Elements busStations = BusService.getDirStationElements(selBLine, selBDir);

    }
}
