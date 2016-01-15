package com.keunsy.wechat.spider.entity;

import java.util.Map;

/**
 * 规则类
 * 
 * 
 */
public class QueryRule {

    private String url;// 链接
    private Map<String, String> paramMap;// 请求map
    private String[] params;// 参数集合
    private String[] values;// 参数对应的值

    /**
     * 对返回的HTML，第一次过滤所用的标签，请先设置type
     */
    private String resultTagName;

    /**
     * CLASS / ID / SELECTION 设置resultTagName的类型，默认为ID
     */
    private int type = ID;

    /**
     * GET / POST 请求的类型，默认GET
     */
    private int requestMoethod = GET;

    public final static int GET = 0;
    public final static int POST = 1;

    public final static int CLASS = 0;
    public final static int ID = 1;
    public final static int SELECTION = 2;
    public final static int ALL = 3;
    public final static int CLASS_MATCH = 4;
    public final static int ID_MATCH = 5;
    public final static int NONE = -1;

    public QueryRule() {
    }

    public QueryRule(String url, String[] params, String[] values, String resultTagName, int type, int requestMoethod) {
	super();
	this.url = url;
	this.params = params;
	this.values = values;
	this.resultTagName = resultTagName;
	this.type = type;
	this.requestMoethod = requestMoethod;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public Map<String, String> getParamMap() {
	return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
	this.paramMap = paramMap;
    }

    public String[] getParams() {
	return params;
    }

    public void setParams(String[] params) {
	this.params = params;
    }

    public String[] getValues() {
	return values;
    }

    public void setValues(String[] values) {
	this.values = values;
    }

    public String getResultTagName() {
	return resultTagName;
    }

    public void setResultTagName(String resultTagName) {
	this.resultTagName = resultTagName;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public int getRequestMoethod() {
	return requestMoethod;
    }

    public void setRequestMoethod(int requestMoethod) {
	this.requestMoethod = requestMoethod;
    }

}
