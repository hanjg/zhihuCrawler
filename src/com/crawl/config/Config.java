package com.crawl.config;

import java.io.IOException;
import java.util.Properties;

/**
 * 通过静态初始化块加载配置文件
 * @author hjg
 */
public class Config {
	/**
	 * 默认解析网页数
	 */
	public static int parsedPageCount;
	/**
	 * 下载网页线程数
	 */
	public static int downloadThreadSize; 
	/**
	 * 知乎账号
	 */
	public static String zhihuAccount;
	/**
	 * 知乎密码
	 */
	public static String zhihuPassword;
	/**
	 * 验证码路径
	 */
	public static String verificationCodePath;
	/**
	 * cookie路径
	 */
	public static String cookiePath;
	/**
	 * 爬虫入口
	 */
	public static String startURL;
	
	/**
	 * 是否使用数据库
	 */
	public static boolean dbEnable;
	/**
	 * 数据库驱动
	 */
	public static String dbDrivers;
	/**
	 * 主机名
	 */
	public static String dbHostName;
	/**
	 * 用户名
	 */
	public static String dbUserName;
	/**
	 * 用户密码
	 */
	public static String dbPassword;
	/**
	 * 使用的数据库名
	 */
	public static String dbName;
	/**
	 * 创建href表
	 */
	public static String createHrefTable;
	/**
	 * 创建user表
	 */
	public static String createUserTable;
	
    static {
    	Properties properties=new Properties();
		try {
			//从根下获取配置文件
			properties.load(Config.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	parsedPageCount=Integer.parseInt(properties.getProperty("parsedPageCount"));
    	downloadThreadSize=Integer.parseInt(properties.getProperty("downloadThreadSize"));
    	zhihuAccount=properties.getProperty("zhihuAccount");
    	zhihuPassword=properties.getProperty("zhihuPassword");
    	verificationCodePath=properties.getProperty("verificationCodePath");
    	cookiePath=properties.getProperty("cookiePath");
    	startURL=properties.getProperty("startURL");
    	dbEnable=Boolean.parseBoolean(properties.getProperty("dbEnable"));
    	if (dbEnable) {
			dbDrivers=properties.getProperty("dbDrivers");
			dbHostName=properties.getProperty("dbHostName");
			dbUserName=properties.getProperty("dbUserName");
			dbPassword=properties.getProperty("dbPassword");
			dbName=properties.getProperty("dbName");
			createHrefTable=properties.getProperty("createHrefTable");
			createUserTable=properties.getProperty("createUserTable");
		}
    }
    public static void main(String[] args) {
		System.out.println(Config.cookiePath);
	}
}
