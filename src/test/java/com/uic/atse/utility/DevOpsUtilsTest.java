package com.uic.atse.utility;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

public class DevOpsUtilsTest {

    @Test
    public void testReplaceHostInUrl(){

        String url = "https://google.com/atse/project1";
        String newDomain = "localhost:80";

        try {
            Assert.assertEquals("https://localhost:80/atse/project1",DevOpsUtils.replaceHostInUrl(url, newDomain));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
