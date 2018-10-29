package com.octopus.core.http;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HTTPInspector {
    private URL url;
    private Proxy proxy;
    private int timeout;
    private boolean isAcceptingRanges = false;
    private long contentLength;
    private String contentType;
    private String fileName;
    private long lastModified;
    private long expiration;
    private String host;
    private int statusCode;
    private Map<String, List<String>> headers;

    public HTTPInspector(URL url, Proxy proxy, int timeout) {
        this.url = url;
        this.proxy = proxy;
        this.timeout = timeout;
    }

    public void Inspect() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setConnectTimeout(timeout);
        connection.setRequestMethod("HEAD");
        connection.connect();

        headers = connection.getHeaderFields();
        statusCode = connection.getResponseCode();
        contentLength = connection.getContentLength();
        contentType = connection.getContentType();
        lastModified = connection.getLastModified();
        expiration = connection.getExpiration();
        host = connection.getHeaderField("Host");

        System.out.println(statusCode);
    }

    public URL getUrl() {
        return url;
    }

    public boolean isAcceptingRanges() {
        return isAcceptingRanges;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getHost() {
        return host;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
