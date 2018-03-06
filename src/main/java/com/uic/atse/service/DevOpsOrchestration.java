package com.uic.atse.service;

import java.util.Map;

/**
 *
 * Orchestration class to orchestrate the devops process
 */
public class DevOpsOrchestration {

    // github service
    private GithubService githubService;

    // Dev-Ops Orchestration process
    private static DevOpsOrchestration orchestration;

    private DevOpsOrchestration(){
        githubService = GithubService.getInstance();
    }

    public static DevOpsOrchestration getInstance(){

        if(null == orchestration){
            orchestration = new DevOpsOrchestration();
            return orchestration;
        }
        return orchestration;
    }

    public void execute(){

        Map<String, String> repoDetails = githubService.getRepoDetailsFromGithub();
        

    }

}
