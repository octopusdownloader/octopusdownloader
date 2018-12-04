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

package org.octopus.core.http;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class HTTPInspectorTest {
    @Test
    public void shouldFindURLWhenNotRedirect() throws Exception {
        HTTPInspector httpInspector = new HTTPInspector(
                new URL("http://localhost:7088/path/to/file.txt"),
                5000,
                5);


        URL finalURL = httpInspector.findRedirectedFinalURL(new URL("http://localhost:7088/path/to/file.txt"), 5);
        assertEquals("http://localhost:7088/path/to/file.txt", finalURL.toString());
    }

    @Test
    public void shouldFindURLWhenRedirect() throws Exception {
        HTTPInspector httpInspector = new HTTPInspector(
                new URL("http://localhost:7088/redirect/path/to/file"),
                5000,
                5);


        URL finalURL = httpInspector.findRedirectedFinalURL(new URL("http://localhost:7088/redirect/path/to/file"), 5);
        assertEquals("http://localhost:7088/path/to/file.txt", finalURL.toString());
    }

    @Test(expected = RedirectLimitException.class)
    public void shouldNotFindURLWhenRedirectLimitExceeded() throws Exception {
        HTTPInspector httpInspector = new HTTPInspector(
                new URL("http://localhost:7088/redirect/path/to/file"),
                5000,
                5);


        URL finalURL = httpInspector.findRedirectedFinalURL(new URL("http://localhost:7088/redirect/path/to/file"), 1);
        assertEquals("http://localhost:7088/path/to/file.txt", finalURL.toString());
    }

    @Test
    public void shouldRetrieveFields() throws Exception {
        HTTPInspector httpInspector = new HTTPInspector(
                new URL("http://localhost:7088/redirect/path/to/file"),
                5000,
                5);


        httpInspector.inspect();
        assertEquals(0, httpInspector.getLastModified());
        assertEquals(0, httpInspector.getExpiration());
        assertEquals(2, httpInspector.getContentLength());
        assertNull(httpInspector.getContentType());
        assertEquals(200, httpInspector.getResponseCode());
        assertTrue(httpInspector.isAcceptingRanges());
        assertEquals("http://localhost:7088/redirect/path/to/file", httpInspector.getOriginalUrl().toString());
        assertEquals("http://localhost:7088/path/to/file.txt", httpInspector.getFinalURL().toString());
        assertEquals("file.txt", httpInspector.getFileName());
        assertEquals(5, httpInspector.getMaxRedirects());
        assertEquals(5000, httpInspector.getTimeout());
    }
}
