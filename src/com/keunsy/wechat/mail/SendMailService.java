package com.keunsy.wechat.mail;

import org.apache.log4j.Logger;

public class SendMailService {

    private static final Logger logger = Logger.getLogger("sendMailLog");

    private static SimpleMailSender sms = new SimpleMailSender();

    public static void sendMail(String subject, String content) {
	// 这个类主要是设置邮件
	MailSenderInfo mailInfo = new MailSenderInfo();
	mailInfo.setMailServerHost("smtp.126.com");
	mailInfo.setMailServerPort("25");
	mailInfo.setValidate(true);
	mailInfo.setUserName("keunsy@126.com");
	mailInfo.setPassword("rong30696179126");// 您的邮箱密码
	mailInfo.setFromAddress("keunsy@126.com");

	mailInfo.setToAddress(new String[] {
	// "2304776186@qq.com",
	"632514396@qq.com"
	// , "2023425183@qq.com"
	});
	mailInfo.setSubject(subject);
	mailInfo.setContent(content);
	// 这个类主要来发送邮件
	boolean flag = sms.sendTextMail(mailInfo);// 发送文体格式
	if (flag) {
	    logger.info("发送成功！");
	} else {
	    logger.info("发送失败！");
	}
    }

    public static void sendMail(String subject, String content, String toAddr[]) {
	// 这个类主要是设置邮件
	MailSenderInfo mailInfo = new MailSenderInfo();
	mailInfo.setMailServerHost("smtp.126.com");
	mailInfo.setMailServerPort("25");
	mailInfo.setValidate(true);
	mailInfo.setUserName("keunsy@126.com");
	mailInfo.setPassword("rong30696179126");// 您的邮箱密码
	mailInfo.setFromAddress("keunsy@126.com");

	mailInfo.setToAddress(toAddr);
	mailInfo.setSubject(subject);
	mailInfo.setContent(content);
	// 这个类主要来发送邮件
	boolean flag = sms.sendHtmlMail(mailInfo);// 发送文体格式
	if (flag) {
	    logger.info("发送成功！");
	} else {
	    logger.info("发送失败！");
	}
    }

    public static void main(String[] args) {
	sendMail("fefe", "fe");
    }
}
