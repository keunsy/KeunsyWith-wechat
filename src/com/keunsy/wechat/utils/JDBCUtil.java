package com.keunsy.wechat.utils;

/**    
 * 文件名：DbUnitTool.java    
 *    
 * 版本信息：    
 * 日期：2015-12-10    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */

/**    
 *     
 * 项目名称：DbBackupTool    
 * 类名称：DbUnitTool    
 * 类描述：    
 * 创建人：chenrong1    
 * 创建时间：2015-12-10 下午1:48:10    
 * 修改人：chenrong1    
 * 修改时间：2015-12-10 下午1:48:10    
 * 修改备注：    
 * @version     
 *     
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.service.FunctionService;
import com.keunsy.wechat.service.ResourceService;

public class JDBCUtil {

    /**
     * 获取普通连接
     * 
     * @method getConnection
     * @return Connection
     */
    public static Connection getConnection(String driverClass, String connectionUrl, String username, String password) {
	Connection conn = null;
	try {
	    Class.forName(driverClass);
	    conn = DriverManager.getConnection(connectionUrl, username, password);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return conn;
    }

    public static Connection getConnection() {

	String driverClass = ResourceService.configPro.getProperty("jdbc.driverClass");
	String connectionUrl = ResourceService.configPro.getProperty("jdbc.url");
	String username = ResourceService.configPro.getProperty("jdbc.username");
	String password = ResourceService.configPro.getProperty("jdbc.password");
	return getConnection(driverClass, connectionUrl, username, password);
    }

    public static void main(String[] args) {
	Connection connection = getConnection();
	List<Function> list = FunctionService.getInstance().getAllFuntionList();
	for (Function function : list) {
	    // System.out.println(function.getTitle().replaceAll("\\n", "\n"));
	    String a = function.getTitle();
	    System.out.println(a);
	    System.out.println(function.getTitle());
	}
	System.out.println("天气功能使用指南\r\n例如：北京天气 \r\n更多方式请自行摸索");
    }

}
