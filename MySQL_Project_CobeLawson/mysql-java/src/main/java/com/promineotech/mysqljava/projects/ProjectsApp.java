package com.promineotech.mysqljava.projects;

import com.promineotech.mysqljava.projects.dao.DbConnection;

public class ProjectsApp {
    //Main method creates DbConnection object and calls the .getConnection() method
    public static void main (String[] args) {
        DbConnection conn = new DbConnection();
        conn.getConnection();
    }
}
