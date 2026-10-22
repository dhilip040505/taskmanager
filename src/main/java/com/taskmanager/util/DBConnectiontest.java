package com.taskmanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectiontest {
public static void main(String[] args) {
	try(Connection con=DBConnection.getConnection()){
		System.out.println("mysql connected succcessfully");
	}catch (SQLException e) {
		e.printStackTrace();
	}
}
	
}
