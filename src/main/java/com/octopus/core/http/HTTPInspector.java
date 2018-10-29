package com.octopus.core.http;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HTTPInspector {
    private URL url;
    private URL finalURL;
    private Proxy proxy;
    private int timeout;
    private boolean isAcceptingRanges = false;
    private long contentLength;
    private String contentType;
    private String fileName;
    private long lastModified;
    private long expiration;
    private String host;
    private int responseCode;
    private int maxRedirects;
    private Map<String, List<String>> headers;

    /**
     * HTTPInspector will inspect a given URL, redirect if necessary
     *
     * @param url          The url to inspect
     * @param proxy        Proxy settings, by default NO_PROXY
     * @param timeout      Timeout in ms
     * @param maxRedirects Maximum number of redirects for inspect, throws RedirectLimitException
     */
    public HTTPInspector(URL url, Proxy proxy, int timeout, int maxRedirects) {
        this.url = url;
        this.proxy = proxy;
        this.timeout = timeout;
        this.maxRedirects = maxRedirects;
    }

    public void inspect() throws Exception {
        this.finalURL = findRedirectedFinalURL(this.url, maxRedirects);

        HttpURLConnection urlConnection = (HttpURLConnection) finalURL.openConnection(proxy);
        urlConnection.setRequestProperty("User-Agent", "OctopusDM");
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();

        if (!urlConnection.getHeaderField("Accept-Ranges").isEmpty()) isAcceptingRanges = true;

    }

    URL findRedirectedFinalURL(URL url, int maxAttempts) throws Exception {
        if (maxAttempts <= 0) {
            throw new RedirectLimitException(maxRedirects);
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(proxy);
        urlConnection.setRequestProperty("User-Agent", "OctopusDM");
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();

        int respCode = urlConnection.getResponseCode();

        if (respCode >= 300 && respCode < 400) {
            String location = urlConnection.getHeaderField("Location");
            if (location == null || location.isEmpty())
                throw new RedirectException("Cannot find the location for redirect");
            URL newLocation = new URL(location);

            return findRedirectedFinalURL(newLocation, maxAttempts - 1);
        }

        return url;
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

    public int getResponseCode() {
        return responseCode;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public int getTimeout() {
        return timeout;
    }

    public long getExpiration() {
        return expiration;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public URL getFinalURL() {
        return finalURL;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }
}
