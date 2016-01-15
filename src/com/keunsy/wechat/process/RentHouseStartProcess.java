package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.renthouse.RentHouseThread;

/**
 * 功能列表处理
 *
 */
public class RentHouseStartProcess implements BasicProcess {

    private RentHouseThread thread = RentHouseThread.getInstance();

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	String title = "开启租房推送！";
	if (!thread.isAlive()) {
	    thread.start();
	} else {
	    title = "已开启租房推送，请勿重复开启";
	}
	List<ArticleResponse> articleList = new ArrayList<ArticleResponse>();
	ArticleResponse article = new ArticleResponse();
	article.setTitle(title);
	articleList.add(article);

	return articleList;

    }

    public static void main(String[] args) {
	new RentHouseStartProcess().excute(null, null);
    }
}
