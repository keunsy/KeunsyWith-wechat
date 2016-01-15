package com.keunsy.wechat.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sword.lang.HttpUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 百度地理编码
 */
public class BaiduGeocodingService {

    public static String URL_PRE = "http://api.map.baidu.com/geocoder/v2/?output=json";

    private final static Logger log = Logger.getLogger(BaiduGeocodingService.class);

    /**
     * 添加ak
     * 
     * @param url
     * @return
     */
    public static String getGeocoding(String address, String city) {

	String url = URL_PRE;
	JSONObject json = null;
	String geocoding = null;

	try {
	    url = BaiduDevService.appendEncodeParam(url, "address", address);
	    if (StringUtils.isNotBlank(city)) {// 空的话 默认为北京市
		url = BaiduDevService.appendParam(url, "city", city);
	    }
	    url = BaiduDevService.appendAK(url);

	    String response = HttpUtils.get(url);
	    json = JSONObject.parseObject(response);
	    if (json.getInteger("status") != 0) {// 表示失败
		return geocoding;
	    }
	    JSONObject result = json.getJSONObject("result");
	    JSONObject location = result.getJSONObject("location");
	    String lat = location.getString("lat");// 纬度
	    String lng = location.getString("lng");// 经度

	    geocoding = lng + "," + lat;// 经纬度

	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}

	return geocoding;
    }

    public static void main(String[] args) {
	BaiduGeocodingService.getGeocoding("望京", "北京市");
    }
}
