package com.promineotech.mysqljava.projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.promineotech.mysqljava.projects.entity.Category;
import com.promineotech.mysqljava.projects.entity.Material;
import com.promineotech.mysqljava.projects.entity.Project;
import com.promineotech.mysqljava.projects.entity.Step;
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

    //This method fetches a list of all the projects from the PROJECT_TABLE and displays the info to the user
    public List<Project> fetchAllProjects() {
        
        //Here we build our SQL code to select all columns from the PROJECT_TABLE and ordered by the id of the projects numerically
        // @formatter:off
        String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_id";
        // @formatter:on

        try(Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);
            
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                try(ResultSet rs = stmt.executeQuery()) {
                    List<Project> projects = new LinkedList<>();

                    while(rs.next()) {
                        projects.add(extract(rs, Project.class));
                    }

                    return projects;
                }
            }

            catch(Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        }

        catch(SQLException e) {
            throw new DbException(e);
        }
    }
    
    //This method fetches a single project by its ID number so the user can manipulate it
	public Optional<Project> fetchProjectById(Integer projectId) {
        
        //Here we build our SQL code to select all columns from the PROJECT_TABLE and ordered by the names of the projects alphabetically
        // @formatter:off
        String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
        // @formatter:on

        try(Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try {
                Project project = null;

                try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                    setParameter(stmt, 1, projectId,Integer.class);

                    try(ResultSet rs = stmt.executeQuery()) {
                        if(rs.next()) {
                            project = extract(rs, Project.class);
                        }
                    }
                }

                //This if statement gathers all the info for the materials, steps, and categories of the project
                if(Objects.nonNull(project)) {
                    project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
                    project.getSteps().addAll(fetchStepsForProject(conn, projectId));
                    project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
                }

                commitTransaction(conn);

                return Optional.ofNullable(project);   
            }

            catch(Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        }

        catch(SQLException e) {
            throw new DbException(e);
        }
    }

    //This method selects all the info from the MATERIAL_TABLE to display to the user
    private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
        // @formatter:off
        String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
        // @formatter:on

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try(ResultSet rs = stmt.executeQuery()) {
                List<Material> materials = new LinkedList<>();

                while(rs.next()) {
                    materials.add(extract(rs, Material.class));
                }

                return materials;
            }
        }
    } 

    //This method selects all the info from the STEP_TABLE to display to the user
    private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
        // @formatter:off
        String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
        // @formatter:on

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try(ResultSet rs = stmt.executeQuery()) {
                List<Step> steps = new LinkedList<>();

                while(rs.next()) {
                    steps.add(extract(rs, Step.class));
                }

                return steps;
            }
        }
    } 
    
    //This method selects all the info from the CATEGORY_TABLE to display to the user
    private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
        // @formatter:off
        String sql = ""
            + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
            + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
            + "WHERE project_id = ?";
        // @formatter:on

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try(ResultSet rs = stmt.executeQuery()) {
                List<Category> categories = new LinkedList<>();

                while(rs.next()) {
                    categories.add(extract(rs, Category.class));
                }

                return categories;
            }
        }
    } 
}
