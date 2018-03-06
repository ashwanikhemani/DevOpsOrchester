package com.uic.atse.service;

import com.offbytwo.jenkins.JenkinsServer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Jenkins Service is used to communicate with the Jenkins server
 */
public class JenkinsService {

    // Jenkins user name
    private String jenkinsUserName;

    // Jenkins user password
    private String jenkinsUserPassword;

    // Jenkins host
    private String jenkinsHost;

    // Jenkins job url with internal IP -------TODO move logic to GitlabSErvice
    private String jenkinsJobUrl;

    // Jenkins config file location
    private String jenkinsConfigFileLocation;

    // Jenkins server
    private JenkinsServer jenkinsServer;

    // Jenkins service
    private static JenkinsService jenkinsService = null;

    private JenkinsService(){

        jenkinsHost = "http://localhost:8080/";
        jenkinsUserName = "root";
        jenkinsUserPassword = "Qwerty123#";
        jenkinsJobUrl = "http://10.0.75.1:8080/project/";
        jenkinsConfigFileLocation = "D:\\jenkins\\config.xml";

        try {
            jenkinsServer = new JenkinsServer(new URI(jenkinsHost),
                    jenkinsUserName, jenkinsUserPassword);

        } catch (URISyntaxException e) {
            System.out.println("Exception occurred while instantiating Jenkins Service");
            e.printStackTrace();
        }
    }

    /**
     * returns the singleton instance of the Jenkins Service
     * @return
     */
    protected static JenkinsService getInstance(){

        if(null == jenkinsService){
            jenkinsService = new JenkinsService();
            return jenkinsService;
        }
        return jenkinsService;
    }

    /**
     * creates a jenkins job
     * @param jobName
     * @param sourceUrl
     * @return
     */
    protected boolean createJob(String jobName, String sourceUrl){
        try {
            String config = getUpdatedConfig(jenkinsConfigFileLocation, jobName, sourceUrl);
            jenkinsServer.createJob(jobName,config, true);
            return true;

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            System.out.println("Exception occurred while creating jenkins job");
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Returns the updated config.xml content for the current job
     * @param templateLocation
     * @param jobName
     * @param gitUrl
     * @return
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws SAXException
     * @throws IOException
     */
    private String getUpdatedConfig(String templateLocation, String jobName, String gitUrl)
    throws ParserConfigurationException, TransformerException, SAXException, IOException {

        DocumentBuilderFactory dcbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dcBuilder = dcbFactory.newDocumentBuilder();
        File file = new File(templateLocation);
        Document document = dcBuilder.parse(new FileInputStream(file));

        Node url = document.getElementsByTagName("url").item(0);
        url.setTextContent(gitUrl);


        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        String content = writer.toString();

        return content;
    }

    protected String getJobUrl(String jobName){
            return jenkinsJobUrl + jobName;

    }


}
