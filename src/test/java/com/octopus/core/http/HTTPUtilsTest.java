package com.octopus.core.http;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class HTTPUtilsTest {
    // Tests for extractFileNameFromURL
    @Test
    public void shouldExtractFileNameFromURLWithNoQuery() throws Exception {
        URL url = new URL("https://somehost:7070/path/to/file.zip");
        assertEquals("file.zip", HTTPUtils.extractFileNameFromURL(url));
    }

    @Test
    public void shouldExtractFileNameFromURLWithQuery() throws Exception {
        URL url = new URL("https://somehost:7070/path/to/file.zip?abc=123&def=34&update=true");
        assertEquals("file.zip", HTTPUtils.extractFileNameFromURL(url));
    }

    @Test
    public void shouldExtractFileNameFromURLWithoutExtension() throws Exception {
        URL url = new URL("https://somehost:7070/path/to/file?abc=123&er=34");
        assertEquals("file", HTTPUtils.extractFileNameFromURL(url));
    }
    //////////////////////////
}
