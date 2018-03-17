package com.uic.atse.utility;

import java.io.*;
import java.util.Properties;

/**
 * DevopsProperties to load properties from properties file
 */
public class DevopsProperties extends Properties {

    private static DevopsProperties devopsProperties = null;

    private String codeDestination;

    private String githubUserName;

    private String githubUserPassword;

    private int projectLimit;

    private String projectLanguage;

    private int projectSizeLowerLimit;

    private String projectType;

    private String gitlabHost;

    private String gitlabUserName;

    private String gitlabUserPassword;

    private String gitlabAccessToken;

    private String jenkinsHost;

    private String jenkinsUserName;

    private String jenkinsUserPassword;

    private String jenkinsJobUrl;

    private String jenkinsConfigFileLocation;

    /**
     * Load all properties
     * @param inStream
     * @throws IOException
     */
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
        this.codeDestination = (String) get("codeDestination");
        this.githubUserName = (String) get("githubUserName");
        this.githubUserPassword = (String) get("githubUserPassword");
        this.projectLanguage = (String) get("projectLanguage");
        this.projectLimit = Integer.parseInt((String) get("projectLimit"));
        this.projectType = (String) get("projectType");
        this.projectSizeLowerLimit = Integer.parseInt((String) get("projectSizeLowerLimit"));

        this.gitlabAccessToken = (String) get("gitlabAccessToken");
        this.gitlabHost = (String) get("gitlabHost");
        this.gitlabUserName = (String) get("gitlabUserName");
        this.gitlabUserPassword = (String) get("gitlabUserPassword");

        this.jenkinsHost = (String) get("jenkinsHost");
        this.jenkinsUserName = (String) get("jenkinsUserName");
        this.jenkinsUserPassword = (String) get("jenkinsUserPassword");
        this.jenkinsConfigFileLocation = (String) get("jenkinsConfigFileLocation");
        this.jenkinsJobUrl = (String) get("jenkinsJobUrl");

    }

    private DevopsProperties(){

        try {
            load(new FileInputStream(new File("devops.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Get singleton instance of Devops Properties
     * @return
     */
    public static DevopsProperties getInstance(){

        if(null == devopsProperties){
            devopsProperties = new DevopsProperties();
        }
        return devopsProperties;
    }

    public String getCodeDestination() {
        return codeDestination;
    }

    public String getGithubUserName() {
        return githubUserName;
    }

    public String getGithubUserPassword() {
        return githubUserPassword;
    }

    public int getProjectLimit() {
        return projectLimit;
    }

    public String getProjectLanguage() {
        return projectLanguage;
    }

    public int getProjectSizeLowerLimit() {
        return projectSizeLowerLimit;
    }

    public String getProjectType() {
        return projectType;
    }

    public String getGitlabHost() {
        return gitlabHost;
    }

    public String getGitlabUserName() {
        return gitlabUserName;
    }

    public String getGitlabUserPassword() {
        return gitlabUserPassword;
    }

    public String getGitlabAccessToken() {
        return gitlabAccessToken;
    }

    public String getJenkinsHost() {
        return jenkinsHost;
    }

    public String getJenkinsUserName() {
        return jenkinsUserName;
    }

    public String getJenkinsUserPassword() {
        return jenkinsUserPassword;
    }

    public String getJenkinsJobUrl() {
        return jenkinsJobUrl;
    }

    public String getJenkinsConfigFileLocation() {
        return jenkinsConfigFileLocation;
    }

    /*public static void main(String[] args){

        DevopsProperties props = DevopsProperties.getInstance();
        System.out.println(props.get("abc"));
    }
*/
}
