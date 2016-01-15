package com.keunsy.wechat.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.keunsy.wechat.dao.BasicDAO;
import com.keunsy.wechat.entity.Function;
import com.keunsy.wechat.utils.EhcacheUtil;
import com.keunsy.wechat.utils.JDBCUtil;

public class FunctionService {

    public static final String CACHE_KEY_SHOW = "cache_key_fuction_show";
    public static final String CACHE_KEY_HIDE = "cache_key_fuction_hide";
    public static final String CACHE_KEY_ALL = "cache_key_fuction_all";
    private static final String SELECT_FUNCTION_SQL = "select * from function";

    private static FunctionService functionService = new FunctionService();

    public static FunctionService getInstance() {
	return functionService;
    }

    /**
     * 获取显示的功能列表
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Function> getShowFuntionList() {
	List<Function> list = (List<Function>) EhcacheUtil.get(EhcacheUtil.CACHE_NAME, CACHE_KEY_SHOW);
	if (list == null) {
	    list = getFuntionList(1);
	    EhcacheUtil.put(EhcacheUtil.CACHE_NAME, CACHE_KEY_SHOW, list);
	}
	return list;
    }

    /**
     * 获取隐藏的功能列表
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Function> getHideFuntionList() {
	List<Function> list = (List<Function>) EhcacheUtil.get(EhcacheUtil.CACHE_NAME, CACHE_KEY_HIDE);
	if (list == null) {
	    list = getFuntionList(2);
	    EhcacheUtil.put(EhcacheUtil.CACHE_NAME, CACHE_KEY_HIDE, list);
	}
	return list;
    }

    /**
     * 获取所有的功能列表
     * 
     * @return
     */
    public List<Function> getAllFuntionList() {
	// List<Function> list = (List<Function>)
	// EhcacheUtil.get(EhcacheUtil.CACHE_NAME, CACHE_KEY_ALL);
	// if (list == null) {
	// list = getFuntionList(0);
	// EhcacheUtil.put(EhcacheUtil.CACHE_NAME, CACHE_KEY_ALL, list);
	// }

	// 调试阶段 先去除缓存
	List<Function> list = getFuntionList(0);
	return list;
    }

    /**
     * 查询
     * 
     * @param type
     * @return
     */
    public List<Function> getFuntionList(int type) {
	Connection conn = JDBCUtil.getConnection();
	String sql = SELECT_FUNCTION_SQL;
	if (type != 0) {
	    sql += "  where type=" + type;
	}
	sql += " order by match_type,id";// 注意顺序
	return BasicDAO.queryList(conn, sql, Function.class);
    }

    /**
     * 
     * @param type
     * @return
     */
    public List<Function> getFuntionListByKeyWord(String keyword) {
	Connection conn = JDBCUtil.getConnection();
	String sql = SELECT_FUNCTION_SQL + "  where keyword='" + keyword + "'";
	return BasicDAO.queryList(conn, sql, Function.class);
    }

    /**
     * 保存（修改或添加）
     * 
     * @param type
     * @return
     * @throws SQLException
     */
    public int savePartFuntion(Function function) throws SQLException {

	StringBuffer sql = new StringBuffer();
	int result = 0;
	Connection conn = JDBCUtil.getConnection();
	// 查询
	List<Function> funcList = getFuntionListByKeyWord(function.getKeyword());
	if (funcList != null && funcList.size() > 1) {// 多条情况不处理
	    return result;
	}
	String url = function.getUrl();

	if (conn == null || conn.isClosed()) {
	    conn = JDBCUtil.getConnection();
	}
	if (funcList != null && funcList.size() > 0) {// 非空则修改
	    sql.append("update function set title='").append(function.getTitle()).append("',match_type=").append(function.getMatch_type());
	    if (StringUtils.isNotBlank(url)) {
		sql.append(",url='" + url + "'");
		sql.append(",pic_url='" + url + "'");
	    }
	    sql.append("  where keyword='" + function.getKeyword() + "'");
	    result = BasicDAO.execute(conn, sql.toString());
	} else {// 空则添加
	    sql.append("insert into function(keyword,title,match_type");
	    if (StringUtils.isNotBlank(url)) {
		sql.append(",url");
		sql.append(",pic_url");
	    }
	    sql.append(") values('").append(function.getKeyword()).append("','").append(function.getTitle()).append("',").append(function.getMatch_type());
	    if (StringUtils.isNotBlank(url)) {
		sql.append(",'").append(url).append("'");
		sql.append(",'").append(url).append("'");
	    }
	    sql.append(")");
	    result = BasicDAO.execute(conn, sql.toString());

	}
	return result;
    }

    public static void main(String[] args) {

	Function tempFunc = new Function();
	tempFunc.setKeyword("游玉玲");
	tempFunc.setTitle("是蠢货a");
	tempFunc.setMatch_type(1);
	tempFunc.setUrl("  ");
	try {
	    FunctionService.getInstance().savePartFuntion(tempFunc);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
}
