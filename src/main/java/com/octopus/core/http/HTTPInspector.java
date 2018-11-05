/*
 * MIT License
 *
 * Copyright (c) 2018 octopusdownloader
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.octopus.core.http;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HTTPInspector {
    private URL url;
    private URL finalURL;
    private int timeout;
    private boolean isAcceptingRanges = false;
    private long contentLength;
    private String contentType;
    private long lastModified;
    private long expiration;
    private int responseCode;
    private int maxRedirects;
    private Map<String, List<String>> headers;

    /**
     * HTTPInspector will inspect a given URL, redirect if necessary
     *
     * @param url          The url to inspect
     * @param timeout      Timeout in ms
     * @param maxRedirects Maximum number of redirects for inspect, throws RedirectLimitException
     */
    public HTTPInspector(URL url, int timeout, int maxRedirects) {
        this.url = url;
        this.timeout = timeout;
        this.maxRedirects = maxRedirects;
    }

    /**
     * Inspects the URL and obtains a stable resource URL with properties.
     *
     * @throws Exception Throws exceptions on failures
     */
    public void inspect() throws Exception {
        finalURL = findRedirectedFinalURL(url, maxRedirects);
        HttpURLConnection urlConnection = (HttpURLConnection) finalURL.openConnection();
        urlConnection.setRequestProperty("User-Agent", "OctopusDM");
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();

        String acceptRanges = urlConnection.getHeaderField("Accept-Ranges");
        if (acceptRanges != null && !acceptRanges.equals("none")) isAcceptingRanges = true;
        contentLength = urlConnection.getContentLength();
        contentType = urlConnection.getContentType();
        lastModified = urlConnection.getLastModified();
        expiration = urlConnection.getExpiration();
        responseCode = urlConnection.getResponseCode();
        headers = urlConnection.getHeaderFields();
    }

    URL findRedirectedFinalURL(URL url, int maxAttempts) throws Exception {
        if (maxAttempts <= 0) {
            throw new RedirectLimitException(maxRedirects);
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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

    public URL getOriginalUrl() {
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
        return HTTPUtils.extractFileNameFromURL(this.finalURL);
    }

    public long getLastModified() {
        return lastModified;
    }

    public int getResponseCode() {
        return responseCode;
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
