package com.keunsy.wechat.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

/**
 * 百度开发工具类
 */
public class BaiduDevService {

    public final static String AK = "DI3itFgh6odxPHNnATvFubjm";
    public static String URL = "";

    /**
     * 添加ak
     * 
     * @param url
     * @return
     */
    public static String appendAK(String url) {

	return url + "&ak=" + AK;
    }

    /**
     * 
     * @param url
     * @return
     */
    public static String appendZoom(String url, int zoom) {

	return url + "&zoom=" + zoom;
    }

    /**
     * 添加中心点
     * 
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String appendCenter(String url, String center) throws UnsupportedEncodingException {

	return url + "&center=" + URLEncoder.encode(center, "utf-8");
    }

    /**
     * 添加标注
     * 
     * @param url
     * @return
     */
    public static String appendMarkers(String url, String center) {

	return url + "&markers=" + center + "&markerStyles=l,Y,0xff0000";
    }

    /**
     * 
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String appendEncodeParam(String url, String param, String value) throws UnsupportedEncodingException {

	return appendParam(url, param, URLEncoder.encode(value, "utf-8"));

    }

    /**
     * 
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String appendParam(String url, String param, String value) throws UnsupportedEncodingException {

	if (StringUtils.isBlank(value)) {
	    return url;
	}
	if (StringUtils.isBlank(param)) {
	    return url + value;
	}

	return url + "&" + param + "=" + value;
    }

    /**
     * 添加标注
     * 
     * @param url
     * @return
     */
    public static String appendSize(String url, String size) {

	return url + size;
    }

    public static void main(String[] args) {
    }
}
