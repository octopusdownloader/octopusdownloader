package com.octopus.core.http;

import java.net.URL;

public class HTTPUtils {
    /**
     * Extract the filename from the URL(with extension)
     *
     * @param url URL that to be inspected
     * @return filename
     */
    public static String extractFileNameFromURL(URL url) {
        String filename;
        String path = url.getPath();
        filename = path.substring(path.lastIndexOf('/') + 1);
        return filename;
    }
}
