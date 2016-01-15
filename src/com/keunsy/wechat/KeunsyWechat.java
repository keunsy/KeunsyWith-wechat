package com.keunsy.wechat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sword.wechat4j.WechatSupport;
import org.sword.wechat4j.response.ArticleResponse;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.process.BasicProcess;
import com.keunsy.wechat.process.TulingApiProcess;
import com.keunsy.wechat.service.FunctionService;
import com.keunsy.wechat.service.ProcessService;

/**
 * Servlet implementation class KunsyWechat
 */
public class KeunsyWechat extends WechatSupport {

    private final static Logger log = Logger.getLogger(KeunsyWechat.class);

    // private final String DEFAULT_PICURL =
    // "https://mmbiz.qlogo.cn/mmbiz/40eQia0BjBYHAssH1kazB83ekZB378SCiclj3mALTHNrnzUxz9JHHmgoxWic2j74oJrzImUTQkVqTz2h6J06icA5CQ/0?wx_fmt=jpeg";
    private final String WRONG_MSG = "很抱歉，貌似出了点问题，请试试别的功能~";
    private final String SORRY_MSG = "很抱歉，受权限限制无法提供此功能！~";
    private final int LIMIT_COUNT = 682;// 文本字数限制
					// 微信对文字长度进行了限制(小于2048个字节,一个utf8中文3个字节)
    private final int LIMIT_SIZE = 10;// 微信图文消息条数限制， 微信对数量有限制，开发模式为10， 编辑模式为8

    static {

    }

    public KeunsyWechat(HttpServletRequest request) {
	super(request);
    }

    @Override
    protected void click() {

    }

    @Override
    protected void kfCloseSession() {

    }

    @Override
    protected void kfCreateSession() {

    }

    @Override
    protected void kfSwitchSession() {

    }

    @Override
    protected void location() {

	responseText(this.wechatRequest.getLabel());

    }

    @Override
    protected void locationSelect() {
	responseText(this.wechatRequest.getLabel());
    }

    @Override
    protected void onImage() {
	responseImage(this.wechatRequest.getMediaId());

    }

    @Override
    protected void onLink() {

    }

    @Override
    protected void onLocation() {
	responseText(this.wechatRequest.getLabel());
    }

    @Override
    protected void onShortVideo() {

	responseVideo(this.wechatRequest.getMediaId(), null, null);
    }

    @Override
    protected void onText() {

	// log.info("onText:\n" + getAllStr());
	String content = this.wechatRequest.getContent().trim();// 用户输入内容
	String fromUserName = this.wechatRequest.getFromUserName().trim();// 用户openid
	List<ArticleResponse> articleList = null;
	boolean flag = false;

	try {
	    // 自定义功能模块
	    List<Function> funcList = FunctionService.getInstance().getAllFuntionList();
	    if (funcList != null) {
		for (Function function : funcList) {
		    String allow_user = function.getAllow_user();
		    String keyword = function.getKeyword();
		    int match_type = function.getMatch_type();
		    // 拥有权限
		    if (StringUtils.isBlank(allow_user) || allow_user.contains(fromUserName)) {
			// 完全匹配或前缀匹配（注意前缀匹配尽量不与完全匹配冲突）
			if ((match_type == 1 && content.equals(keyword)) || (match_type == 2 && content.startsWith(keyword))) {
			    // 反射执行相应的方法
			    String class_name = function.getClass_name();
			    BasicProcess process = ProcessService.getProcessObj(class_name);
			    articleList = process.excute(function, content);
			    flag = true;
			    break;
			}
		    }
		}
	    }
	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	}

	try {
	    if (!flag) {// 默认交予图灵处理
		log.info("the content[" + content + "] did not match function!");
		articleList = new TulingApiProcess().excute(null, content);
	    }
	    // 出错处理
	    if (articleList == null || articleList.size() == 0) {
		articleList = new ArrayList<ArticleResponse>();
		ArticleResponse article = new ArticleResponse();
		article.setTitle(WRONG_MSG);
		articleList.add(article);
	    }

	    // 进行值回传
	    if (articleList.size() == 1 && StringUtils.isBlank(articleList.get(0).getUrl()) && StringUtils.isBlank(articleList.get(0).getPicUrl())
		    && StringUtils.isBlank(articleList.get(0).getDescription())) {
		String text = articleList.get(0).getTitle();
		text = StringUtils.isNotBlank(text) ? text : WRONG_MSG;// 防止为空
		// 注意 br 标签转化为 换行\n
		if (text.contains("<br>")) {
		    text = text.replaceAll("<br>", "\n");
		} else if (text.contains("<br/>")) {
		    text = text.replaceAll("<br/>", "\n");
		}
		if (text.length() > LIMIT_COUNT) {
		    text = text.substring(0, LIMIT_COUNT - 4) + "...";
		}
		responseText(text);
	    } else {
		if (articleList.size() > LIMIT_SIZE) {
		    articleList = articleList.subList(0, LIMIT_SIZE);
		}
		responseNews(articleList);
	    }
	} catch (Exception e) {
	    log.error("", e);
	    e.printStackTrace();
	    responseText(WRONG_MSG);
	}

    }

    @Override
    protected void onUnknown() {

    }

    @Override
    protected void onVideo() {
	responseVideo(this.wechatRequest.getMediaId(), null, null);
    }

    @Override
    protected void onVoice() {

	responseVoice(this.wechatRequest.getMediaId());
    }

    @Override
    protected void picPhotoOrAlbum() {

    }

    @Override
    protected void picSysPhoto() {

    }

    @Override
    protected void picWeixin() {

    }

    @Override
    protected void scan() {

    }

    @Override
    protected void scanCodePush() {

    }

    @Override
    protected void scanCodeWaitMsg() {

    }

    @Override
    protected void subscribe() {

    }

    @Override
    protected void templateMsgCallback() {

    }

    @Override
    protected void unSubscribe() {

    }

    @Override
    protected void view() {

    }

    /**
     * 获取参数
     * 
     * @return
     */
    public String getAllStr() {
	return "wechatRequest [ToUserName=" + this.wechatRequest.getToUserName() + ", FromUserName=" + this.wechatRequest.getFromUserName() + ", CreateTime=" + this.wechatRequest.getCreateTime()
		+ ", MsgType=" + this.wechatRequest.getMsgType() + ", Event=" + this.wechatRequest.getEvent() + ", EventKey=" + this.wechatRequest.getEventKey() + ", MsgId="
		+ this.wechatRequest.getMsgId() + ", Content=" + this.wechatRequest.getContent() + ", Location_X=" + this.wechatRequest.getLocation_X() + ", Location_Y="
		+ this.wechatRequest.getLocation_Y() + ", Scale=" + this.wechatRequest.getScale() + ", Label=" + this.wechatRequest.getLabel() + ", Title=" + this.wechatRequest.getTitle()
		+ ", Description=" + this.wechatRequest.getDescription() + ", Url=" + this.wechatRequest.getUrl() + ", PicUrl=" + this.wechatRequest.getPicUrl() + ", MediaId="
		+ this.wechatRequest.getMediaId() + ", Format=" + this.wechatRequest.getFormat() + ", Status=" + this.wechatRequest.getStatus() + ", Latitude=" + this.wechatRequest.getLatitude()
		+ ", Longitude=" + this.wechatRequest.getLongitude() + ", Precision=" + this.wechatRequest.getPrecision() + ", Ticket=" + this.wechatRequest.getTicket() + ", ThumbMediaId="
		+ this.wechatRequest.getThumbMediaId() + ", ScanCodeInfo=" + this.wechatRequest.getScanCodeInfo() + ", SendPicsInfo=" + this.wechatRequest.getSendPicsInfo() + ", SendLocationInfo="
		+ this.wechatRequest.getSendLocationInfo() + "]";
    }

    /**
     * 默认回复
     */
    public void defaultReply() {
	log.info(getAllStr());
	responseText(SORRY_MSG);
    }

}
