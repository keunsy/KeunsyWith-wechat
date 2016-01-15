package com.keunsy.wechat.entity;

import java.io.Serializable;

public class Function implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String keyword; // 关键词
    private String title; // 回复标题
    private String desc; // 描述
    private String url; // 链接地址
    private String pic_url; // 图片地址
    private String allow_user; // 允许操作的用户，默认所有人都可以操作
    private String class_name; // 反射的类名，默认 AskReplyProcess
    private int type;// 类型 1：显示 2：隐藏 默认隐藏
    private int match_type;// 关键词匹配类型 1：完全匹配 2：前缀匹配 默认完全匹配
    private String wrong_msg;// 失败提示信息

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getKeyword() {
	return keyword;
    }

    public void setKeyword(String keyword) {
	this.keyword = keyword;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getDesc() {
	return desc;
    }

    public void setDesc(String desc) {
	this.desc = desc;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getPic_url() {
	return pic_url;
    }

    public void setPic_url(String pic_url) {
	this.pic_url = pic_url;
    }

    public String getAllow_user() {
	return allow_user;
    }

    public void setAllow_user(String allow_user) {
	this.allow_user = allow_user;
    }

    public String getClass_name() {
	return class_name;
    }

    public void setClass_name(String class_name) {
	this.class_name = class_name;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public int getMatch_type() {
	return match_type;
    }

    public void setMatch_type(int match_type) {
	this.match_type = match_type;
    }

    public String getWrong_msg() {
	return wrong_msg;
    }

    public void setWrong_msg(String wrong_msg) {
	this.wrong_msg = wrong_msg;
    }

}
