package com.promineotech.mysqljava.projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.promineotech.mysqljava.projects.exception.DbException;

public class DbConnection {
    //Initializing variables that will be static for my connection info
    private static String HOST = "localhost";
    private static String PASSWORD = "1234Five";
    private static int PORT = 3306;
    private static String SCHEMA = "projects";
    private static String USER = "root";

    //Method that takes my connection variables and establishes link to MySQL Server
    public static Connection getConnection() {
        String uri = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s", HOST, PORT, SCHEMA, USER, PASSWORD);

        //Try-Catch to determine if connection to server is successful or not
        try {
            Connection conn = DriverManager.getConnection(uri);
            System.out.println("Connection to schema '" + SCHEMA + "' is successful.");
            return conn;
        }
        catch (SQLException e) {
            System.out.println("Unable to get connection at " + uri);
            throw new DbException("Unable to get connection at " + uri);
        }
    }
}
