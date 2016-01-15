package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.qrcode.QRCodeUtil;

/**
 * 普通问答处理
 *
 */
public class QRCodeProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(QRCodeProcess.class);

    private final String KEYWORD = "二维码";
    private final static String PATH_POST = "qrcode/";
    private final static String URL_PRE = "http://101.200.239.232/KeunsyWith-wechat/" + PATH_POST;// 暂时写死，后期动态

    private static String destPath;// 保存路径

    static {
	// 获取存储路径
	String path_pre = QRCodeProcess.class.getClassLoader().getResource("/").getPath();
	String path = path_pre.substring(0, path_pre.indexOf("WEB-INF"));
	destPath = path + PATH_POST;
    }

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;
	try {
	    String text = content.replaceFirst(KEYWORD, "").trim();
	    String fileName = QRCodeUtil.encode(text, destPath);
	    articleList = new ArrayList<ArticleResponse>();
	    ArticleResponse article = new ArticleResponse();
	    // 获取资源路径
	    String url = URL_PRE + fileName;
	    article.setTitle(text);
	    article.setDescription(function.getDesc());
	    article.setPicUrl(url);
	    article.setUrl(url);
	    articleList.add(article);
	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}
	// 防止找不到
	if (articleList == null || articleList.size() == 0) {
	    articleList = new ArrayList<ArticleResponse>();
	    ArticleResponse article = new ArticleResponse();
	    article.setTitle(function.getWrong_msg());
	    articleList.add(article);
	}

	return articleList;

    }

    public static void main(String[] args) {
	new QRCodeProcess().excute(null, "二维码 fe");
    }
}
