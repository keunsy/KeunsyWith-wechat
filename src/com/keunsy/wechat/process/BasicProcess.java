package com.keunsy.wechat.process;

import java.util.List;

import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;

/**
 * 消息处理程序
 * 
 * @author chenrong
 *
 */
public interface BasicProcess {

    public List<ArticleResponse> excute(Function function, String content);

}
