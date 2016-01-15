package com.keunsy.wechat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.keunsy.wechat.KeunsyWechat;

/**
 * Servlet implementation class WechatServlet
 */
public class WechatServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final static Logger log = Logger.getLogger(WechatServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public WechatServlet() {
	super();

    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	// String requestUrl = request.getRequestURL().toString();// 得到请求的URL地址

	// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");

	KeunsyWechat keunsyWechat = new KeunsyWechat(request);
	String result = keunsyWechat.execute();
	response.getOutputStream().write(result.getBytes());
	// 打印日志
	log.info(keunsyWechat.getAllStr());

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	this.doGet(request, response);
    }

}
