/*
 * MIT License
 *
 * Copyright (c) 2019 octopusdownloader
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

package org.octopus.downloads.handlers;

import org.octopus.core.http.HTTPInspector;
import org.octopus.downloads.DownloadFile;
import org.octopus.settings.OctopusSettings;

import java.net.URL;
import java.util.ArrayList;

public class HttpDownloadHandler implements DownloadHandler {
    public static final long TEN_MB = 1024 * 10 * 10;
    private HTTPInspector httpInspector;
    private int numChunks = 1;

    @Override
    public ArrayList<DownloadFile> createFileChunks(URL url) throws Exception {
        ArrayList<DownloadFile> downloadFiles = new ArrayList<>();

        httpInspector = new HTTPInspector(url, 5000, 5);
        httpInspector.inspect();

        numChunks = calculateNumberOfChunks(httpInspector);
        long contentLength = httpInspector.getContentLength();

        if (contentLength == -1) {
            downloadFiles.add(new DownloadFile())
        }

        return null;
    }

    private int calculateNumberOfChunks(HTTPInspector httpInspector) {
        long len = httpInspector.getContentLength();

        if (!httpInspector.isAcceptingRanges()) return 1;
        if (httpInspector.getContentLength() == -1) return 1;
        if (len < TEN_MB) return 1;

        return OctopusSettings.getMaxDownloadParts();
    }
}
