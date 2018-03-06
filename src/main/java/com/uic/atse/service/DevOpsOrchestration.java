package com.uic.atse.service;

import org.gitlab.api.models.GitlabProject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Orchestration class to orchestrate the devops process
 */
public class DevOpsOrchestration {

    // github service
    private GithubService githubService;

    // Gitlab service
    private GitlabService gitlabService;

    // Jenkins service
    private JenkinsService jenkinsService;

    // Dev-Ops Orchestration process
    private static DevOpsOrchestration orchestration = null;

    // Source code destination
    private String codeDestination;

    private DevOpsOrchestration(){
        githubService = GithubService.getInstance();
        gitlabService = GitlabService.getInstance();
        jenkinsService = JenkinsService.getInstance();
        codeDestination = "D://gitrepo/";
    }

    /**
     * returns a singleton object of the DevopsOrchestration class
     * @return
     */
    public static DevOpsOrchestration getInstance(){

        if(null == orchestration){
            orchestration = new DevOpsOrchestration();
            return orchestration;
        }
        return orchestration;
    }

    /**
     * performs the dev-ops orchestration
     */
    public void execute(){

        // Get repository details from Github according to search query
        Map<String, String> repoDetails = githubService.getRepoDetailsFromGithub();

        // Clone repositories to file system
        Map<String, String> codeLocationMap =  new HashMap<>();
        repoDetails.keySet().parallelStream().forEach(repoUrl -> {

            githubService.cloneRepositoryFromGithub(repoUrl, codeDestination + "/" + repoDetails.get(repoUrl));
            codeLocationMap.put(repoDetails.get(repoUrl), codeDestination + "/" + repoDetails.get(repoUrl));
        });

        // Create gitlab project and jenkins job
        codeLocationMap.entrySet().parallelStream().forEach(entry -> {

            String projectName = entry.getKey() + "3";
            // gitlab project
            GitlabProject project = gitlabService.createProject(projectName);

            // jenkins job
            jenkinsService.createJob(projectName, project.getHttpUrl());

            // add hook from gitlab project to jenkins job
            gitlabService.addHookToProject(project, jenkinsService.getJobUrl(projectName));

            // push code to gitlab
            gitlabService.pushCode(entry.getValue(), project.getHttpUrl());

        });
    }

}
