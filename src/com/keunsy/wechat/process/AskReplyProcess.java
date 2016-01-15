package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;

/**
 * 普通问答处理
 *
 */
public class AskReplyProcess implements BasicProcess {

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = new ArrayList<ArticleResponse>();
	ArticleResponse article = new ArticleResponse();
	article.setTitle(function.getTitle());
	if (function.getType() == 1 && function.getTitle().startsWith("使用说明")) {// 使用说明前+表情图片，连接类不需要
	    article.setTitle("\ue142" + function.getTitle());
	}
	article.setDescription(function.getDesc());
	article.setPicUrl(function.getPic_url());
	article.setUrl(function.getUrl());
	articleList.add(article);

	return articleList;

    }
}
