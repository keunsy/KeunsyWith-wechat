package com.keunsy.wechat.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * ehcache 缓存工具类
 * 
 * cacheName在ehcache.xml中配置
 */
public class EhcacheUtil {

    public static CacheManager manager = CacheManager.create();
    public static final String CACHE_NAME = "mainCache";
    public static final String DOWNLOAD_MOVIE_CACHE = "downloadMovieCache";
    public static final String DOUBAN_MOVIE_CACHE = "doubanMovieCache";
    public static final String EMOTION_CACHE = "emotionCache";
    public static final String WEATHER_CACHE = "weatherCache";
    public static final String VIDEO_VIP_CACHE = "videoVipCache";
    public static final String BUS_CACHE = "busCache";
    public static final String VPN_CACHE = "vpnCache";

    public static Object get(String cacheName, Object key) {
	Cache cache = manager.getCache(cacheName);
	if (cache != null) {
	    Element element = cache.get(key);
	    if (element != null) {
		return element.getObjectValue();
	    }
	}
	return null;
    }

    public static void put(String cacheName, Object key, Object value) {
	Cache cache = manager.getCache(cacheName);
	if (cache != null) {
	    cache.put(new Element(key, value));
	}
    }

    public static boolean remove(String cacheName, Object key) {
	Cache cache = manager.getCache(cacheName);
	if (cache != null) {
	    return cache.remove(key);
	}
	return false;
    }

    public static void main(String[] args) {
	String key = "key";
	String value = "hello";
	EhcacheUtil.put(DOWNLOAD_MOVIE_CACHE, key, value);
	System.out.println(EhcacheUtil.get(DOWNLOAD_MOVIE_CACHE, key));
    }

}
