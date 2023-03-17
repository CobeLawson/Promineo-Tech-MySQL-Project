package com.promineotech.mysqljava.projects.service;

import com.promineotech.mysqljava.projects.entity.Project;
import com.promineotech.mysqljava.projects.exception.DbException;

import java.util.List;
import java.util.NoSuchElementException;
import com.promineotech.mysqljava.projects.dao.ProjectDao;

public class ProjectService {
    private ProjectDao projectDao = new ProjectDao();

    //This method calls the DAO class to insert a new project row
    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }

    //This method calls the DAO class to fetch and display all listed projects to the user
    public List<Project> fetchAllProjects() {
        return projectDao.fetchAllProjects();
    }

    //This method calls the DAO class to fetch and display a specific project that the user specifies
    public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(()
             -> new NoSuchElementException(
                "Project with project ID= " + projectId 
                + " does not exist." ));
    }

    //This method calls the DAO class to update and change the details of a project the user selects
    public void modifyProjectDetails(Project project) {
        if(!projectDao.modifyProjectDetails(project)) {
            throw new DbException("Project with ID= " + project.getProjectId() + " does not exist.");
        }
    }

    //This method calls the DAO class to delete a project from our database
    public void deleteProject(Integer projectId) {
        if(!projectDao.deleteProject(projectId)) {
            throw new DbException("Project with ID= " + projectId + " does not exist.");
        }
    }
}
