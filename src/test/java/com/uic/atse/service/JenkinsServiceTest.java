package com.uic.atse.service;

import com.offbytwo.jenkins.JenkinsServer;
import com.uic.atse.utility.DevopsProperties;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

public class JenkinsServiceTest {

    @Test
    public void testCreateJob(){
        JenkinsService jenkinsService = JenkinsService.getInstance();
        DevopsProperties props = DevopsProperties.getInstance();

        jenkinsService.createJob("java-nio-server-2","https://github.com/jjenkov/java-nio-server.git");

        try {
            JenkinsServer jenkinsServer = new JenkinsServer(new URI(props.getJenkinsHost()),
                    props.getJenkinsUserName(), props.getJenkinsUserPassword());

            Assert.assertEquals(1,jenkinsServer.getJobs().entrySet().stream()
                    .filter(entry -> entry.getValue().getName().equals("java-nio-server-2"))
                    .collect(Collectors.toList()).size());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
