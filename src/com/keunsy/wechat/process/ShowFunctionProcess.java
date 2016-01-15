package com.keunsy.wechat.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.FunctionService;

/**
 * 功能列表处理
 *
 */
public class ShowFunctionProcess implements BasicProcess {

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	StringBuffer sbBuffer = new StringBuffer();
	List<Function> funcList = FunctionService.getInstance().getAllFuntionList();
	// 祝福语
	if (StringUtils.isNotBlank(function.getTitle())) {
	    sbBuffer.append("\ue032" + function.getTitle() + "\ue032\n");
	}
	sbBuffer.append("\ue11d欢迎关注KeunsyWith\n\ue11d回复下列关键词查看说明").append("\n\n");
	for (Function temp : funcList) {
	    /*
	     * ?(注后续需要添加显示权限)
	     */
	    if (!temp.getKeyword().equals(content) && temp.getType() == 1) {// 功能不显示，隐藏的不显示
		sbBuffer.append("\ue32e  ").append(temp.getKeyword()).append("\n");
	    }
	}
	sbBuffer.append("\n").append("\ue11d订制功能请联系Keunsy");
	sbBuffer.append("\n").append("\ue11d持续更新中，敬请期待");
	List<ArticleResponse> articleList = new ArrayList<ArticleResponse>();
	ArticleResponse article = new ArticleResponse();
	article.setTitle(sbBuffer.toString());
	articleList.add(article);

	return articleList;

    }
}
