package com.uic.atse.service;

import com.uic.atse.utility.DevOpsUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Gitlab service to communicate with Gitlab server
 */
public class GitlabService {

    // Gitlab server host
    private String gitlabHost;

    // Gitlab server username
    private String gitlabUserName;

    // Gitlab server password
    private String gitlabUserPassword;

    // Gitlab access token
    private String gitlabAccessToken;

    // gitlabAPI
    private GitlabAPI gitlabAPI;

    private static GitlabService gitlabService = null;

    private GitlabService(){
        gitlabHost = "http://localhost";
        gitlabUserName = "root";
        gitlabUserPassword = "Qwerty123#";
        gitlabAccessToken = "hKA4pufmsYz1nGyvAo-H";
        gitlabAPI = GitlabAPI.connect(gitlabHost,gitlabAccessToken);

    }

    /**
     * returns a singleton instance of Gitlab Service
     * @return
     */
    protected static GitlabService getInstance(){

        if(null == gitlabService){
            gitlabService = new GitlabService();
            return gitlabService;
        }
        return gitlabService;

    }

    /**
     * add hook from Gitlab project to jenkins job
     * @param project
     * @param jenkinsJobUrl
     */
    protected boolean addHookToProject(GitlabProject project, String jenkinsJobUrl){
        try {
            gitlabAPI.addProjectHook(project, jenkinsJobUrl);
            return true;

        } catch (IOException e) {

            System.out.println("Exception occurred while adding hook from project " + project.getName()
                    + " to jenkins job at "+ jenkinsJobUrl);
            e.printStackTrace();
            return false;
        }
    }

    protected GitlabProject createProject(String projectName){
        GitlabProject project = null;
        try {

            project = gitlabAPI.createProject(projectName);
            project.setHttpUrl(DevOpsUtils.replaceHostInUrl(project.getHttpUrl(),
                    gitlabHost.split("://")[1]));

        } catch (IOException | URISyntaxException e) {
            System.out.println("Exception occurred while creating project " + projectName);
            e.printStackTrace();
        }

        return project;
    }

    protected void pushCode(String sourceDirectoryPath, String remoteUrl){

        Git git = null;
        try {
            git = Git.open(new File(sourceDirectoryPath));

            git.push()
                    .setRemote(remoteUrl)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitlabUserName, gitlabUserPassword))
                    .call();

        } catch (IOException | GitAPIException e) {
            System.out.println("Exception occurred while pushing code to Gitlab from directory at " + sourceDirectoryPath );
            e.printStackTrace();
        }

    }


}
