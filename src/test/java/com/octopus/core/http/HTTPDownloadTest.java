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

import com.octopus.core.Downloader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HTTPDownloadTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void shouldGetRange() throws Exception {
        HTTPDownload download = new HTTPDownload(
                new URL("http://localhost/path/to/file"),
                0,
                0);
        assertEquals(download.getRange(), "bytes=0-");
    }

    @Test
    public void shouldDownloadFullTest() throws Exception {
        HTTPInspector httpInspector = new HTTPInspector(
                new URL("http://localhost:7088/data/image.jpg"),
                5000,
                5
        );
        httpInspector.inspect();

        HTTPDownload httpDownload = new HTTPDownload(
                new URL("http://localhost:7088/data/image.jpg"),
                0,
                0);

        Downloader downloader = new Downloader(httpDownload, Paths.get(tmpFolder.getRoot().getCanonicalPath(), "sample.jpg"));
        downloader.download();

        File file = new File(tmpFolder.getRoot().getCanonicalPath(), "sample.jpg");
        assertTrue("Check file exists", file.exists());
        assertEquals("Is file size equal", httpInspector.getContentLength(), file.length());
    }

    @Test
    public void shouldDownloadPartialTest() throws Exception {
        HTTPDownload httpDownload = new HTTPDownload(
                new URL("http://localhost:7088/data/partial/image.jpg"),

                0,
                1023);

        Downloader downloader = new Downloader(httpDownload, Paths.get(tmpFolder.getRoot().getCanonicalPath(), "sample.part"));
        downloader.download();

        File file = new File(tmpFolder.getRoot().getCanonicalPath(), "sample.part");
        assertTrue("Check file exists", file.exists());
        assertEquals("Is file size equal", 1024, file.length());
    }
}