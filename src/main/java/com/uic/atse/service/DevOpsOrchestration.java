package com.uic.atse.service;

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

    // Dev-Ops Orchestration process
    private static DevOpsOrchestration orchestration;

    // Source code destination
    private String codeDestination;

    private DevOpsOrchestration(){
        githubService = GithubService.getInstance();
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
        Set<String> codeLocationSet = repoDetails.keySet().parallelStream().map(repoUrl -> {
            githubService.cloneRepositoryFromGithub(repoUrl, codeDestination+"/" + repoDetails.get(repoUrl));
            return codeDestination+"/" + repoDetails.get(repoUrl);
        }).collect(Collectors.toSet());

        System.out.println(codeLocationSet);
    }

}
