package com.keunsy.wechat.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.keunsy.wechat.renthouse.RentHouseThread;

public class RentHouseListener implements ServletContextListener {

    private RentHouseThread thread = RentHouseThread.getInstance();

    public void contextDestroyed(ServletContextEvent e) {

	if (thread != null && !thread.isInterrupted()) {
	    thread.interrupt();
	}
    }

    /**
     * 加载启动
     */
    public void contextInitialized(ServletContextEvent e) {

	thread.start(); // servlet 上下文初始化时启动 socket
    }
}
