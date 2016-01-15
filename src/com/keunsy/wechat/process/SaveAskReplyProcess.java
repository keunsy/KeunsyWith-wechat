package com.keunsy.wechat.process;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.FunctionService;

/**
 * 添加，修改
 *
 */
public class SaveAskReplyProcess implements BasicProcess {

    private final static Logger log = Logger.getLogger(SaveAskReplyProcess.class);
    private final String KEYWORD = "保存";

    @Override
    public List<ArticleResponse> excute(Function function, String content) {

	List<ArticleResponse> articleList = null;

	try {
	    String info = content.replaceFirst(KEYWORD, "").trim();
	    String info_array[] = info.split("\\s+");
	    String keyword = info_array[0].trim();// 关键词
	    String title = info_array[1].trim();// 标题
	    String match_type = info_array[2].trim();// 匹配类型
	    String url = null;
	    if (info_array.length == 4) {
		url = info_array[3].trim();// 图片链接
	    }
	    Function tempFunc = new Function();
	    tempFunc.setKeyword(keyword);
	    tempFunc.setTitle(title);
	    tempFunc.setMatch_type(Integer.parseInt(match_type));
	    tempFunc.setUrl(url);
	    // 添加或修改
	    int result = FunctionService.getInstance().savePartFuntion(tempFunc);
	    if (result > 0) {
		articleList = new ArrayList<ArticleResponse>();
		ArticleResponse article = new ArticleResponse();
		article.setTitle(StringUtils.isNotBlank(function.getTitle()) ? function.getTitle() : "成功！");
		articleList.add(article);
	    }
	} catch (SQLException e) {
	    log.error("", e);
	    e.printStackTrace();
	}
	// 防止错误
	if (articleList == null || articleList.size() == 0) {
	    articleList = new ArrayList<ArticleResponse>();
	    ArticleResponse article = new ArticleResponse();
	    article.setTitle(function.getWrong_msg());
	    articleList.add(article);
	}

	return articleList;

    }

    public static void main(String[] args) {

	new SaveAskReplyProcess().excute(null, "保存 游玉玲  蠢货一个 2 ");
    }
}
