package com.crawl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.crawl.config.Config;
import com.crawl.entity.User;
import com.crawl.util.SimpleLogger;

/**
 * 知乎数据存取对象
 * 
 * @author hjg
 *
 */
public class ZhiHuDAO {
	private static Logger logger = SimpleLogger.getSimpleLogger(ZhiHuDAO.class);

	/**
	 * 数据库表初始化。 如果不存在，则创建数据库表； 如果存在的话，则不创建。
	 * 
	 * @param connection
	 */
	public static void DBTablesInit(Connection connection) {
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			resultSet = connection.getMetaData().getTables(null, null, "href", null);
			statement = connection.createStatement();
			if (!resultSet.next()) {
				statement.execute(Config.createHrefTable);
				logger.info("href表创建成功");
			} else {
				logger.info("href表已存在");
			}
			resultSet = connection.getMetaData().getTables(null, null, "user", null);
			if (!resultSet.next()) {
				statement.execute(Config.createUserTable);
				logger.info("user表创建成功");
			} else {
				logger.info("user表已存在");
			}
			statement.close();
			resultSet.close();
			connection.close();
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}

	}

	/**
	 * 判断该数据库中是否存在符合sql语句条件的数据
	 * 
	 * @param connection
	 * @param sql
	 * @return
	 */
	public synchronized static boolean isContain(Connection connection, String sql) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				int num = resultSet.getInt("count(*)");
				if (num == 0) {
					return false;
				} else {
					return true;
				}
			}
			preparedStatement.close();
			resultSet.close();
		} catch (SQLException e) {
			logger.info("SQLException", e);
		}

		return true;
	}

	/**
	 * user 插入数据库
	 * 
	 * @param user
	 * @throws SQLException
	 */
	public synchronized static boolean insertToDB(User user) {
		Connection connection = DBConnectionManage.getConnection();
		String isContainSql = "select count(*) from user WHERE userId='" + user.getUserId() + "'";
		if (isContain(connection, isContainSql)) {
			logger.info("数据库已经存在该用户:" + user.getName());
			return false;
		}
		String columns = "userId,hashId,name,location,bussiness,gender,voteCount,"
				+ "thankedCount,questionCount,answerCount,articlesCount," + "followingCount,followerCount,school";
		String values = "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		String sql = "insert into user (" + columns + ") values(" + values + ")";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, user.getUserId());
			preparedStatement.setString(2, user.getHashId());
			preparedStatement.setString(3, user.getName());
			preparedStatement.setString(4, user.getLocation());
			preparedStatement.setString(5, user.getBusiness());
			preparedStatement.setString(6, user.getGender());
			preparedStatement.setInt(7, user.getVoteupCount());
			preparedStatement.setInt(8, user.getThankedCount());
			preparedStatement.setInt(9, user.getQuestionCount());
			preparedStatement.setInt(10, user.getAnswerCount());
			preparedStatement.setInt(11, user.getArticlesCount());
			preparedStatement.setInt(12, user.getFollowingCount());
			preparedStatement.setInt(13, user.getFollowerCount());
			preparedStatement.setString(14, user.getSchool());
			preparedStatement.executeUpdate();
			preparedStatement.close();
			connection.close();
			logger.info("插入数据库成功:" + user.getName());
		} catch (SQLException e) {
			logger.info("SQLException", e);
		}
		return true;
	}

	/**
	 * 将访问过的url插入数据库
	 * 
	 * @param md5Href
	 *            经过md5处理后的url
	 * @return
	 * @throws SQLException
	 */
	public synchronized static boolean insertHref(String md5Href) {
		String isContainSql = "select count(*) from href WHERE href='" + md5Href + "'";
		Connection connection = DBConnectionManage.getConnection();
		try {
			if (isContain(connection, isContainSql)) {
				logger.debug("数据库已经存在该url:" + md5Href);
				return false;
			}
			String sql = "insert into href (href) values (?)";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, md5Href);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.debug("url插入成功:" + md5Href);
		return true;
	}

	/**
	 * 清空href表
	 * 
	 * @param cn
	 * @throws SQLException
	 */
	public synchronized static void deleteHrefTable(Connection connection) {
		try {
			String sql = "delete from href";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			logger.info("href表删除成功");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DBTablesInit(DBConnectionManage.getConnection());
	}
}
