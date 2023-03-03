package com.promineotech.mysqljava.projects.service;

import com.promineotech.mysqljava.projects.entity.Project;
import com.promineotech.mysqljava.projects.dao.ProjectDao;

public class ProjectService {
    private ProjectDao projectDao = new ProjectDao();

    //This method calls the DAO class to insert a new project row
    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }

}
