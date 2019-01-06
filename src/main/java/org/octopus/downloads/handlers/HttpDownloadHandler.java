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

import org.octopus.core.Downloader;
import org.octopus.core.http.HTTPDownload;
import org.octopus.core.http.HTTPInspector;
import org.octopus.core.misc.ProgressReporter;
import org.octopus.settings.OctopusSettings;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HttpDownloadHandler implements DownloadHandler {
    public static final long TEN_MB = 1024 * 10 * 10;
    private HTTPInspector httpInspector;
    private long contentLength = -1;
    private ProgressReporter progressReporter;
    private URL url;
    private Path baseTempDirectory;
    private ArrayList<Path> tempFilePaths = new ArrayList<>();

    public HttpDownloadHandler(URL url, ProgressReporter progressReporter) throws Exception {
        this.progressReporter = progressReporter;
        this.url = url;
        httpInspector = new HTTPInspector(url, 5000, 10);
        httpInspector.inspect();

        this.contentLength = httpInspector.getContentLength();
    }

    public void setBaseTempDirectory(Path baseTempDirectory) {
        this.baseTempDirectory = baseTempDirectory;
    }

    @Override
    public ArrayList<Path> getTempFilePaths() {
        return tempFilePaths;
    }

    @Override
    public ArrayList<Downloader> getDownloaders() {
        ArrayList<Downloader> downloaders = new ArrayList<>();

        int numChunks = calculateNumberOfChunks(httpInspector);
        URL finalUrl = httpInspector.getFinalURL();

        if (numChunks == 1) {
            HTTPDownload download = new HTTPDownload(finalUrl, 0);
            Path path = Paths.get(baseTempDirectory.toString(), "part0");
            tempFilePaths.add(path);
            Downloader downloader = new Downloader(0, download, path, progressReporter);
            downloaders.add(downloader);

            return downloaders;
        }

        long chunkSize = contentLength / numChunks;
        long remaining = contentLength % numChunks;

        for (int i = 0; i < numChunks; i++) {
            long from = chunkSize * i;
            long to = chunkSize * (i + 1) - 1;
            if (i == numChunks - 1) to += remaining;
            System.out.println("from :" + from + ", to: " + to);

            HTTPDownload download = new HTTPDownload(finalUrl, from, to);
            Path path = Paths.get(baseTempDirectory.toString(), String.format("part%d", i));
            tempFilePaths.add(path);
            Downloader downloader = new Downloader(i, download, path, progressReporter);
            downloaders.add(downloader);
        }

        System.out.println("downloader size " + downloaders.size());

        return downloaders;
    }

    @Override
    public String fileName() {
        return httpInspector.getFileName();
    }

    @Override
    public long fileSize() {
        return contentLength;
    }

    private int calculateNumberOfChunks(HTTPInspector httpInspector) {
        long len = httpInspector.getContentLength();
        System.out.println("Content Length " + len);

        if (!httpInspector.isAcceptingRanges()) return 1;
        if (httpInspector.getContentLength() == -1) return 1;
        if (len < TEN_MB) return 1;
        if (len < 50 * TEN_MB) return 2;
        if (len < 100 * TEN_MB) return 4;

        return OctopusSettings.getMaxDownloadParts();
    }
}
