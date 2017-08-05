package com.crawl.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.crawl.config.Config;
import com.crawl.util.SimpleLogger;

/**
 * 管理数据库连接
 * @author hjg
 *
 */
public class DBConnectionManage{
	private static Logger logger = SimpleLogger.getSimpleLogger(DBConnectionManage.class);
	
	private static Connection connection;
	
	public static Connection getConnection(){
		try {
			if (connection==null||connection.isClosed()) {
				connection=createConnection();
				logger.debug("数据库连接创建成功");
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}
		return connection;
	}
	public static void close(){
		try {
			if (connection!=null) {
				connection.close();
				logger.debug("数据库连接关闭成功");
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}
	}
	private static Connection createConnection(){
		String host=Config.dbHostName;
		String user=Config.dbUserName;
		//注意：配置文件中的属性开头可以有空格，但是最后不能有空格，否则......
		String password=Config.dbPassword;
		String dbName=Config.dbName;
		//数据库url
		String url="jdbc:mysql://"+host+":3306/"+dbName;

		Connection connection=null;
		try {
//			System.setProperty("jdbc.drivers", Config.dbDrivers);
			connection=DriverManager.getConnection(url, user, password);
			
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}
		return connection;
	}
	public static void main(String [] args) throws Exception{
		getConnection();
		getConnection();
		System.out.println(connection);
		close();
		System.out.println(connection);
	}
}