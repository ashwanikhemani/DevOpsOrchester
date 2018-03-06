package com.uic.atse.utility;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class DevOpsUtils {

    public static String replaceHostInUrl(String originalURL,
                                          String newAuthority)
            throws URISyntaxException {

        URI uri = new URI(originalURL);
        uri = new URI(uri.getScheme().toLowerCase(Locale.US), newAuthority,
                uri.getPath(), uri.getQuery(), uri.getFragment());

        return uri.toString();
    }
}
