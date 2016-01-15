package com.keunsy.wechat.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.service.ExtractService;

/**
 * 图片爬虫业务 ，暂时只支持 搜狗图片,备份使用 一般不用
 */
public class PicUrlService {

    private final static Logger log = Logger.getLogger(PicUrlService.class);

    // private final static String URL =
    // "http://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=index&fr=&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=";
    // http://pic.sogou.com/pics?query=%B7%C7%D7%A1&mood=0&picformat=0&mode=1&di=2&w=05009900&dr=1&_asf=pic.sogou.com&_ast=1451981901&start=48&reqType=ajax&tn=0&reqFrom=result

    private final static String URL = "http://pic.sogou.com/pics?query=";
    private final static String SOSO_REGEX = "\"smallThumbUrl\":\".*\"";
    private final static int LIMIT_COUNT = 9;

    public static List<String> getBaiduPic(String word) {

	QueryRule rule = null;
	List<String> picList = null;

	// 查询
	try {
	    String finalUrl = URL + URLEncoder.encode(word.trim(), "gbk");// 该网站要求gbk编码
	    picList = new ArrayList<String>();

	    rule = new QueryRule();
	    rule.setUrl(finalUrl);
	    rule.setType(QueryRule.ALL);
	    rule.setRequestMoethod(QueryRule.POST);
	    Elements results = ExtractService.extract(rule);
	    /*
	     * 此网页返回的数据在head的json对象中 ，此处采用硬编码方式解析得出图片地址，后续可改为其他更加合理方式
	     */
	    String html = results.html();

	    String array[] = html.split(SOSO_REGEX);
	    String temp = html;
	    for (int i = 0; i < array.length; i++) {
		temp = temp.replace(array[i], "");
	    }
	    array = temp.split(",");
	    for (int i = 0; i < array.length; i++) {
		if (array[i].matches(SOSO_REGEX)) {
		    picList.add(array[i].replace("\"smallThumbUrl\":\"", "").replace("\"", ""));
		    if (picList.size() == LIMIT_COUNT) {
			break;
		    }
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    log.error(null, e);
	}

	return picList;
    }

    public static void main(String[] args) {
	PicUrlService.getBaiduPic("非");
	// System.out.println(URLEncoder.encode("非住"));
	// try {
	// System.out.println(URLEncoder.encode("非住", "gbk"));
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
    }
}
