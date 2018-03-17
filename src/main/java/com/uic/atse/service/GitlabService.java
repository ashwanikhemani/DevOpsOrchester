package com.uic.atse.service;

import com.uic.atse.utility.DevOpsUtils;
import com.uic.atse.utility.DevopsProperties;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        DevopsProperties props = DevopsProperties.getInstance();

        gitlabHost = props.getGitlabHost();
        gitlabUserName = props.getGitlabUserName();
        gitlabUserPassword = props.getGitlabUserPassword();
        gitlabAccessToken = props.getGitlabAccessToken();
        gitlabAPI = GitlabAPI.connect(gitlabHost,gitlabAccessToken);

    }

    /**
     * returns a singleton instance of Gitlab Service
     * @return
     */
    protected static GitlabService getInstance(){

        if(null == gitlabService){
            gitlabService = new GitlabService();
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

            Map<String, Integer> existingProjectNameIds = gitlabAPI.getProjects().stream()
                    .collect(Collectors.toMap(proj -> proj.getName(), proj -> proj.getId()));

            if(existingProjectNameIds.keySet().contains(projectName)){

                System.out.println("Project already exists in gitlab for " + projectName);
                project = gitlabAPI.getProject(existingProjectNameIds.get(projectName));

                /*gitlabAPI.deleteProject(existingProjectNameIds.get(projectName));
                System.out.println("Deleting gitlab project");
                Thread.sleep(10000); // adding a pause to make sure project is deleted before re-creation*/
            }
            else {
                project = gitlabAPI.createProject(projectName);
            }
            project.setHttpUrl(DevOpsUtils.replaceHostInUrl(project.getHttpUrl(),
                    gitlabHost.split("://")[1]));

        } catch (IOException | URISyntaxException e) {
            System.out.println("Exception occurred while creating project " + projectName);
            e.printStackTrace();
        }

        return project;
    }

    protected boolean pushCode(String sourceDirectoryPath, String remoteUrl){

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
            return false;
        }
        return true;
    }

    /*public static void main(String[] args) throws IOException {
        GitlabService gitlabService = GitlabService.getInstance();
        gitlabService.gitlabAPI.getProjects().stream().forEach(proj -> {
            try {
                gitlabService.gitlabAPI.deleteProject(proj.getId());
                System.out.println("Deleting "+proj.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }*/

}
