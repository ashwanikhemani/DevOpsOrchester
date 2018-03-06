package com.uic.atse.service;


import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
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
    private static GitHubClient gitHubClient;

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
                    .filter(repo -> !repo.isPrivate() && repo.getSize() > projectSizeLowerLimit)
                    .limit(projectLimit)
                    .map(repo -> {

                try{
                    Repository repository = repositoryService.getRepository(() -> repo.generateId());
                    return repository;

                } catch (IOException e){
                    System.out.println("Exception occurred while getting repo details for ID " + repo.generateId());
                    e.printStackTrace();
                    return null;
                }

            }).collect(Collectors.toMap((repo) -> repo.getCloneUrl(), (repo) -> repo.getName()));

        } catch (IOException e) {
            System.out.println("Exception occurred while getting repositories from Github");
            e.printStackTrace();
        }

        return repoMap;
    }

    protected void cloneRepositoryFromGithub(String cloneUrl, String destination){
        cloneRepositoryFromGithub(cloneUrl, destination, true);
    }

    protected void cloneRepositoryFromGithub(String cloneUrl, String destination, boolean cloneAllBranches){
        try {
            Git git = Git.cloneRepository()
                    .setURI(cloneUrl)
                    .setDirectory(new File(destination))
                    .setCloneAllBranches(cloneAllBranches)
                    .call();

        } catch (GitAPIException e){
            System.out.println("Exception occurred while cloning repository " + cloneUrl);
            e.printStackTrace();
        }
    }






}
