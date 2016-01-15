package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.renthouse.DoubanHouseService;
import com.keunsy.wechat.renthouse.RentHouseThread;

/**
 * 功能列表处理
 *
 */
public class RentHouseStopProcess implements BasicProcess {

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	DoubanHouseService.stop();
	RentHouseThread.stopRentHouse();
	List<ArticleResponse> articleList = new ArrayList<ArticleResponse>();
	ArticleResponse article = new ArticleResponse();
	article.setTitle("已停止租房推送！");
	articleList.add(article);

	return articleList;

    }

    public static void main(String[] args) {
	new RentHouseStopProcess().excute(null, null);
    }
}
