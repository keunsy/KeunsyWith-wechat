package com.keunsy.wechat.entity;

import java.io.Serializable;

public class WeiboMonitor implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;//
    private String weibo_addr;// 微博地址，微博id
    private String weibo_name;// 微博归属谁
    private String receive_emails;// 接收邮箱
    private String real_addr;// 实际微博完整地址
    private int status;// 是否启用 1：启用 -1：不启用
    private String insert_time;//
    private String update_time;//

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getWeibo_addr() {
	return weibo_addr;
    }

    public void setWeibo_addr(String weibo_addr) {
	this.weibo_addr = weibo_addr;
    }

    public String getWeibo_name() {
	return weibo_name;
    }

    public void setWeibo_name(String weibo_name) {
	this.weibo_name = weibo_name;
    }

    public String getReceive_emails() {
	return receive_emails;
    }

    public void setReceive_emails(String receive_emails) {
	this.receive_emails = receive_emails;
    }

    public String getReal_addr() {
	return real_addr;
    }

    public void setReal_addr(String real_addr) {
	this.real_addr = real_addr;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public String getInsert_time() {
	return insert_time;
    }

    public void setInsert_time(String insert_time) {
	this.insert_time = insert_time;
    }

    public String getUpdate_time() {
	return update_time;
    }

    public void setUpdate_time(String update_time) {
	this.update_time = update_time;
    }

}
