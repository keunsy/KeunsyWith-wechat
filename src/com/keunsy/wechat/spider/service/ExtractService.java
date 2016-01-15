package com.keunsy.wechat.spider.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.exception.RuleException;

public class ExtractService {

    /**
     * @param rule
     * @return
     * @throws IOException
     */
    public static Elements extract(QueryRule rule) throws IOException {

	// 进行对rule的必要校验
	validateRule(rule);

	Elements results = null;
	String url = rule.getUrl();
	String resultTagName = rule.getResultTagName();
	int type = rule.getType();
	int requestType = rule.getRequestMoethod();

	Connection conn = Jsoup.connect(url);
	// 设置查询参数
	if (rule.getParamMap() != null) {
	    conn.data(rule.getParamMap());
	}
	// 获取document
	Document doc = getDocument(conn, requestType);
	// System.out.println(doc.html());
	// 过滤html
	results = dealElements(doc, resultTagName, type);

	return results;
    }

    private static Document getDocument(Connection conn, int requestType) throws IOException {
	switch (requestType) {
	case QueryRule.GET:
	    return conn.userAgent("Mozilla").timeout(100000).get();
	case QueryRule.POST:
	    return conn.userAgent("Mozilla").timeout(100000).post();
	default:
	    throw new RuleException("请求类型错误！");
	}
    }

    /**
     * 过滤html
     * 
     * @param doc
     * @param resultTagName
     * @param type
     * @return
     */
    private static Elements dealElements(Document doc, String resultTagName, int type) {
	if (StringUtils.isBlank(resultTagName) && type != QueryRule.ALL) {
	    return doc.getElementsByTag("body");
	} else {
	    switch (type) {
	    case QueryRule.CLASS:
		return doc.getElementsByClass(resultTagName);
	    case QueryRule.ID:
		Elements elements = new Elements();
		elements.add(doc.getElementById(resultTagName));
		return elements;
	    case QueryRule.SELECTION:
		return doc.select(resultTagName);
	    case QueryRule.ALL:
		return doc.getAllElements();
	    case QueryRule.CLASS_MATCH:
		return doc.getElementsByAttributeValueMatching("class", resultTagName);
	    case QueryRule.ID_MATCH:
		return doc.getElementsByAttributeValueMatching("id", resultTagName);
	    default:
		return doc.getElementsByTag(resultTagName);
	    }
	}
    }

    /**
     * 对传入的参数进行必要的校验
     */
    private static void validateRule(QueryRule rule) {

	String url = rule.getUrl();
	if (StringUtils.isBlank(url)) {
	    throw new RuleException("url不能为空！");
	}
	if (!url.startsWith("http://")) {
	    throw new RuleException("url的格式不正确！");
	}

	if (rule.getParams() != null && rule.getValues() != null) {
	    if (rule.getParams().length != rule.getValues().length) {
		throw new RuleException("参数的键值对个数不匹配！");
	    }
	}

    }

}
