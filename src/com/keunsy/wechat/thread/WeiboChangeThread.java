package com.keunsy.wechat.thread;

import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import com.keunsy.wechat.entity.WeiboMonitor;
import com.keunsy.wechat.mail.SendMailService;
import com.keunsy.wechat.service.WeiboService;
import com.keunsy.wechat.spider.entity.QueryRule;
import com.keunsy.wechat.spider.service.ExtractService;

public class WeiboChangeThread extends Thread {

    private final static Logger log = Logger.getLogger(WeiboChangeThread.class);

    private WeiboMonitor weiBoMonitor;// 微博
    private QueryRule rule;// 规则

    public WeiboChangeThread(WeiboMonitor weiBoMonitor, QueryRule rule) {
	this.weiBoMonitor = weiBoMonitor;
	this.rule = rule;
    }

    private String[] cid_array = new String[2];// 记录id的数组

    private boolean isRuning = true;// 是否运行

    @Override
    public void run() {

	String mailSubject = weiBoMonitor.getWeibo_name() + "微博更新提示邮件";
	String mailContent = "您关注的" + weiBoMonitor.getWeibo_name() + "更新微博啦，<a href='" + weiBoMonitor.getReal_addr() + "'>点击跳转 </a>";
	String[] toAddr = weiBoMonitor.getReceive_emails().split(",");
	int errorCount = 0;

	while (isRuning) {
	    try {
		// 获取元素
		Elements cEles = ExtractService.extract(rule);
		// 获取前两个id（存在置顶的情况）
		String cid0 = cEles.get(0).attr("id");
		String cid1 = cEles.get(1).attr("id");
		// 对比前两个id是否一致（第一次只存不对比）
		// 不一致，发送邮件，同时更改id
		if ((cid_array[0] != null && !cid0.equals(cid_array[0])) || (cid_array[1] != null && !cid1.equals(cid_array[1]))) {
		    SendMailService.sendMail(mailSubject, mailContent, toAddr);
		}
		cid_array[0] = cid0;
		cid_array[1] = cid1;
		// 测试使用
		// cid_array[0] = "";
		// cid_array[1] = "";
		Thread.sleep(60000);// 一分钟
	    } catch (Exception e) {
		log.error("", e);
		e.printStackTrace();
		if (errorCount++ > 5) {
		    SendMailService.sendMail("错误：" + mailSubject, "检测微博监控错误次数过多！");
		    break;
		}
	    }
	}

    }

    public static void main(String[] args) {

	WeiboMonitor weiBoMonitor = new WeiboMonitor();
	weiBoMonitor.setWeibo_addr("2508776370");
	weiBoMonitor.setWeibo_name("允儿");
	weiBoMonitor.setReal_addr("http://weibo.com/linyuner90?is_all=1");
	weiBoMonitor.setReceive_emails("632514396@qq.com");
	WeiboService weiboService = WeiboService.getInstance();
	QueryRule rule = weiboService.getWeiboQueryRule(weiBoMonitor.getWeibo_addr());

	WeiboChangeThread weiboChangeThread = new WeiboChangeThread(weiBoMonitor, rule);
	weiboChangeThread.run();
    }
}
