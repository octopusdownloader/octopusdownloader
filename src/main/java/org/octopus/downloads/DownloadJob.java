/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 by octopusdownloader
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

package org.octopus.downloads;

import org.octopus.core.Downloader;
import org.octopus.core.misc.ProgressReporter;
import org.octopus.downloads.handlers.DownloadHandler;
import org.octopus.downloads.handlers.HttpDownloadHandler;
import org.octopus.exceptions.UnknownProtocolException;
import org.octopus.settings.OctopusSettings;
import org.octopus.utils.FileMerger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadJob {
    private URL url;
    private String fileName;
    private Path baseDirectory;
    private ProgressReporter progressReporter;
    private ExecutorService executorService;
    private long fileSize = -1;
    private Path tempDownloadFolder;
    private ArrayList<Downloader> downloaders = null;
    private JobState state = JobState.Started;
    private DownloadHandler downloadHandler;
    private List<Future<Long>> futures;

    public DownloadJob(URL url, Path baseDirectory, String fileName) {
        this.url = url;
        this.baseDirectory = baseDirectory;
        this.fileName = fileName;
        this.progressReporter = new ProgressReporter();
        this.futures = new ArrayList<>();
    }

    public void prepareDownload() throws Exception {
        switch (url.getProtocol()) {
            case "http":
            case "https":
                downloadHandler = new HttpDownloadHandler(url, this.progressReporter);
                if (this.fileName.isEmpty()) this.fileName = downloadHandler.fileName();
                tempDownloadFolder = createTempDownloadDirectory();
                downloadHandler.setBaseTempDirectory(tempDownloadFolder);
                break;

            default:
                throw new UnknownProtocolException(url.getProtocol());
        }

        this.downloaders = downloadHandler.getDownloaders();
        this.fileSize = downloadHandler.fileSize();
        this.executorService = Executors.newCachedThreadPool();
    }

    protected Path createTempDownloadDirectory() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String prefix = String.format("%s_%s", timeStamp, fileName);

        return Files.createDirectories(Paths.get(OctopusSettings.getInstance().getTempDownloadBasepath().toString(), prefix));
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Path getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public ArrayList<Path> getTempFilePaths() {
        return downloadHandler.getTempFilePaths();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ProgressReporter getProgressReporter() {
        return progressReporter;
    }

    public void download() throws Exception {
        downloaders.stream().
                map(downloader -> executorService.submit(downloader)).forEach(future -> this.futures.add(future));
        executorService.shutdown();

        for (Future<Long> future : futures) future.get();
    }

    public void interruptDownload() {
        executorService.shutdownNow();
    }

    public JobState getState() {
        return state;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void moveToDestination() throws Exception {
        ArrayList<Path> paths = downloadHandler.getTempFilePaths();
        Files.createDirectories(baseDirectory);
        if (paths.size() > 1) {
            FileMerger.AppendFiles(paths);
        }


        Files.move(paths.get(0), Paths.get(baseDirectory.toString(), fileName));
    }

    @Override
    public String toString() {
        return "DownloadJob{" +
                "url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", baseDirectory=" + baseDirectory +
                '}';
    }
}
