package com.promineotech.mysqljava.projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.promineotech.mysqljava.projects.entity.Project;
import com.promineotech.mysqljava.projects.exception.DbException;

import provided.util.DaoBase;

/*
 * This Class uses JDBC to perform CRUD operations on the project tables.
 */

public class ProjectDao extends DaoBase {
    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";

    //This Method inserts a project row into the project table
    //It takes in a parameter of the object we are entering
    //And it returns the project with the primary key
    public Project insertProject(Project project) {

        //Here we build our SQL code used to actually manipulate the database
        // @formatter:off
        String sql = ""
            + "INSERT INTO " + PROJECT_TABLE + " "
            + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
            + "VALUES "
            + "(?, ?, ?, ?, ?)";
        // @formatter:on

        try(Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            //This try-catch will set the parameters & fields needed to build the project within the database
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                stmt.executeUpdate();

                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
                commitTransaction(conn);

                project.setProjectId(projectId);
                return project;
            }
            
            //Catching & throwing errors when inserting the row
            catch(Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        }
        catch(SQLException e) {
            throw new DbException(e);
        }
    }
}
