package com.uic.atse.service;

import com.uic.atse.utility.DevopsProperties;
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
        DevopsProperties props = DevopsProperties.getInstance();

        githubService = GithubService.getInstance();
        gitlabService = GitlabService.getInstance();
        jenkinsService = JenkinsService.getInstance();
        codeDestination = props.getCodeDestination();
    }

    /**
     * returns a singleton object of the DevopsOrchestration class
     * @return
     */
    public static DevOpsOrchestration getInstance(){

        if(null == orchestration){
            orchestration = new DevOpsOrchestration();
        }
        return orchestration;
    }

    /**
     * performs the dev-ops orchestration
     */
    public void execute(){

        System.out.println("Starting Dev-ops pipeline");

        // Get repository details from Github according to search query
        Map<String, String> repoDetails = githubService.getRepoDetailsFromGithubUsingTopic();

        // Clone repositories to file system
        Map<String, String> codeLocationMap =  new HashMap<>();
        repoDetails.keySet().parallelStream().forEach(repoUrl -> {

            if(githubService.cloneRepositoryFromGithub(repoUrl,
                    codeDestination + "/" + repoDetails.get(repoUrl)))
                codeLocationMap.put(repoDetails.get(repoUrl), codeDestination + "/" + repoDetails.get(repoUrl));
        });

        // Create gitlab project and jenkins job
        codeLocationMap.entrySet().parallelStream().forEach(entry -> {

            String projectName = entry.getKey();
            // gitlab project
            GitlabProject project = gitlabService.createProject(projectName);
            if(null == project){
                System.out.println("Gitlab project creation failed for project " + projectName);
                return;
            }

            // jenkins job
            if(!jenkinsService.createJob(projectName, project.getHttpUrl())){
                System.out.println("Jenkins job creation failed for project " + projectName);
                return;
            }

            // add hook from gitlab project to jenkins job
            if(!gitlabService.addHookToProject(project, jenkinsService.getJobUrl(projectName))){
                System.out.println("Hook creation failed for project " + projectName);
                return;
            }

            // push code to gitlab
            if(!gitlabService.pushCode(entry.getValue(), project.getHttpUrl())){
                System.out.println("Code push to Gitlab failed for project " + projectName);
                return;
            }

        });

        System.out.println("Done!");
    }

}
