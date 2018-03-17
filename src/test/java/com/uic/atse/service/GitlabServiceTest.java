package com.uic.atse.service;

import com.uic.atse.utility.DevopsProperties;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabProjectHook;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GitlabServiceTest {

    @Test
    public void testCreateProject(){

        DevopsProperties props = DevopsProperties.getInstance();

        GitlabService gitlabService = GitlabService.getInstance();
        gitlabService.createProject("test-project");

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(props.getGitlabHost(),props.getGitlabAccessToken());
            List<GitlabProject> projects = gitlabAPI.getProjects();
            if(projects.stream()
                    .filter(proj -> proj.getName().equals("test-project"))
                    .collect(Collectors.toList()).size() == 0){
                Assert.fail();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testAddHook(){

        DevopsProperties props = DevopsProperties.getInstance();

        GitlabService gitlabService = GitlabService.getInstance();
        JenkinsService jenkinsService = JenkinsService.getInstance();

        GitlabProject project = gitlabService.createProject("test-project-3");
        jenkinsService.createJob("test-project-3",project.getHttpUrl());

        Assert.assertTrue(gitlabService.addHookToProject(project,jenkinsService.getJobUrl("test-project-3")));

        GitlabAPI gitlabAPI = GitlabAPI.connect(props.getGitlabHost(),props.getGitlabAccessToken());
        try {
            List<GitlabProjectHook> hooks = gitlabAPI.getProjectHooks(project);

            if(hooks.size() ==0)
                Assert.fail();

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testPushCode(){

        DevopsProperties props = DevopsProperties.getInstance();

        int randomNum = new Random().nextInt(100);
        String projectName = "joda-time"+ randomNum;

        GitlabService gitlabService = GitlabService.getInstance();
        GithubService githubService = GithubService.getInstance();
        githubService.cloneRepositoryFromGithub("https://github.com/JodaOrg/joda-time.git",
                props.getCodeDestination()+"/"+projectName);

        GitlabProject project = gitlabService.createProject(projectName);

        Assert.assertTrue(gitlabService.pushCode(props.getCodeDestination()+"/"+projectName, project.getHttpUrl()));

        LsRemoteCommand ls = new LsRemoteCommand(null);
        ls.setRemote(project.getHttpUrl()).setCredentialsProvider(
                new UsernamePasswordCredentialsProvider(props.getGitlabUserName(), props.getGitlabUserPassword()));
        try {
            ls.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            Assert.fail();
        }


    }
}
