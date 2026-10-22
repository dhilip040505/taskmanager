package com.taskmanager.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {
  public static final String URL="jdbc:mysql://localhost:3306/taskmanager_db";
  public static final String USER="root";
  public static final String PASSWORD="";
  
  public static Connection getConnection() throws SQLException {
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        throw new SQLException(
	            "MySQL driver is not visible to the application", e
	        );
	    }

	    return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
