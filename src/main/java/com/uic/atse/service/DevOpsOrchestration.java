package com.uic.atse.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

        System.out.println(codeLocationMap);

        /*repoDetails.keySet().parallelStream().forEach(projectName -> {

        });*/

        // Create gitlab project from
    }

}
