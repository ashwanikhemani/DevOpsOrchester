package com.uic.atse.service;


import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Github Service clones repositories from Github
 *
 */
public class GithubService {

    private String githubUserName;

    private String githubUserPassword;

    // number of projects to be cloned
    private int projectLimit;

    // programming language of the projects
    private String projectLanguage;

    // lower size limit of each project
    private int projectSizeLowerLimit;

    // github client
    private GitHubClient gitHubClient;

    // github repository service
    private RepositoryService repositoryService;

    private static GithubService githubService = null;

    private GithubService(){
        githubUserName = "ateamhasnoname03";
        githubUserPassword = "qwerty123#";
        projectLimit = 20;
        projectLanguage = "Java";
        projectSizeLowerLimit = 100;
        gitHubClient = new GitHubClient();
        gitHubClient.setCredentials(githubUserName, githubUserPassword);
        repositoryService = new RepositoryService(gitHubClient);
    }

    /**
     * returns a singleton github service
     * @return
     */
    protected static GithubService getInstance(){

        if (null == githubService) {
            githubService = new GithubService();
            return githubService;
        }
        return githubService;

    }

    protected Map<String, String> getRepoDetailsFromGithub(){
        Map<String,String> repoMap = null;

        try {

            Map<String, String> searchQuery = new HashMap<>();
            searchQuery.put("keyword", projectLanguage);
            List<SearchRepository> searchResults = null;

            searchResults = repositoryService.searchRepositories(searchQuery);

            repoMap = searchResults.parallelStream()
                    .filter(repo -> !repo.isPrivate() && repo.getSize() > projectLimit)
                    .limit(projectSizeLowerLimit)
                    .map(repo -> {

                try{
                    Repository repository = repositoryService.getRepository(() -> repo.generateId());
                    return repository;

                } catch (IOException e){
                    System.out.println("Exception occurred while getting repo details for ID " + repo.generateId());
                    e.printStackTrace();
                    return null;
                }

            }).collect(Collectors.toMap((repo) -> repo.getName(), (repo) -> repo.getCloneUrl()));

        } catch (IOException e) {
            System.out.println("Exception occurred while getting repositories from Github");
            e.printStackTrace();
        }

        return repoMap;
    }






}
