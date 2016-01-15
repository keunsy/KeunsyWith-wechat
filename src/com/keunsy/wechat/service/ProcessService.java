package com.keunsy.wechat.service;

import com.keunsy.wechat.process.BasicProcess;

/**
 * 
 * 
 * 
 */
public class ProcessService {

    private static final String CLASS_NAME_PRE = "com.keunsy.wechat.process.";

    /**
     * 类反射
     * 
     * @param class_name
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static BasicProcess getProcessObj(String class_name) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

	Class clz = Class.forName(CLASS_NAME_PRE + class_name);

	return (BasicProcess) clz.newInstance();
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	getProcessObj("ShowFunctionProcess").excute(null, "");
    }
}
