package com.uic.atse.service;

import com.uic.atse.utility.DevopsProperties;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


public class GithubServiceTest {

    @Test
    public void testGetRepoDetailsFromGithub(){

        GithubService githubService = GithubService.getInstance();
        Map<String, String> repoDetails = githubService.getRepoDetailsFromGithub();

        if(null == repoDetails || repoDetails.size() == 0)
            Assert.fail();

        for(Map.Entry<String,String> entry : repoDetails.entrySet()){

            LsRemoteCommand ls = new LsRemoteCommand(null);
            ls.setRemote(entry.getKey());
            try {
                ls.call();
            } catch (GitAPIException e) {
                e.printStackTrace();
                Assert.fail();
            }
        }


    }

    @Test
    public void testGetRepoDetailsFromGithubUsingTopic(){
        GithubService githubService = GithubService.getInstance();
        Map<String, String> repoDetails = githubService.getRepoDetailsFromGithubUsingTopic();

        if(null == repoDetails || repoDetails.size() == 0)
            Assert.fail();

        for(Map.Entry<String,String> entry : repoDetails.entrySet()){

            LsRemoteCommand ls = new LsRemoteCommand(null);
            ls.setRemote(entry.getKey());
            try {
                ls.call();
            } catch (GitAPIException e) {
                //e.printStackTrace();
                Assert.fail();
            }
        }
    }

    @Test
    public void testCloneRepositoryFromGithub(){

        GithubService githubService = GithubService.getInstance();
        DevopsProperties props = DevopsProperties.getInstance();

        githubService.cloneRepositoryFromGithub("https://github.com/JodaOrg/joda-time.git",
                props.getCodeDestination()+"/joda-time");

        if(!Files.exists(Paths.get(props.getCodeDestination()+"/joda-time")))
            Assert.fail();

        try {
            Git.open(new File(props.getCodeDestination()+"/joda-time"));

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }


}
