package com.crawl;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {
	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/bbs";
		String user = "root";
		String password = "hjg19931031";
		try {
			DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("SQL");
			e.printStackTrace();
		}
	}
}
