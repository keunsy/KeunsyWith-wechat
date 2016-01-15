package com.keunsy.wechat.listener;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.keunsy.wechat.entity.WeiboMonitor;
import com.keunsy.wechat.service.WeiboService;
import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.thread.WeiboChangeThread;

public class WeiboMonitorListener implements ServletContextListener {

    private final static Logger log = Logger.getLogger(WeiboMonitorListener.class);

    /**
     * 加载启动
     */
    public void contextInitialized(ServletContextEvent arg0) {

	WeiboService weiboService = WeiboService.getInstance();
	List<WeiboMonitor> list = weiboService.getWeiboMonitorList();
	if (list != null) {
	    for (WeiboMonitor weiboMonitor : list) {
		try {
		    if (weiboMonitor.getStatus() == 1) {// 开启的状态
			QueryRule rule = weiboService.getWeiboQueryRule(weiboMonitor.getWeibo_addr());
			WeiboChangeThread weiboChangeThread = new WeiboChangeThread(weiboMonitor, rule);
			weiboChangeThread.start();
		    }
		} catch (Exception e) {
		    log.error(null, e);
		    e.printStackTrace();
		}
	    }
	}

    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

    }
}
