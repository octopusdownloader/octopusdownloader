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

package org.octopus.downloads;

import javafx.concurrent.Task;
import org.octopus.core.misc.ProgressEvent;
import org.octopus.core.misc.ProgressReporter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadTask extends Task<Void> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss");
    private ProgressReporter progressReporter;
    private int id;
    private long fileSize;
    private DownloadJob downloadJob;
    private Date startedTime;
    private JobState jobState;

    public DownloadTask(int id, DownloadJob job) {
        this.id = id;
        this.downloadJob = job;
        this.progressReporter = downloadJob.getProgressReporter();

        this.startedTime = new Date();
        this.progressReporter.addPropertyChangeListener(
                ProgressEvent.OnBytesReceived,
                evt -> {
                    updateProgress((Long) evt.getNewValue(), fileSize);
                }
        );

        this.progressReporter.addPropertyChangeListener(
                ProgressEvent.OnStatusChanged,
                evt -> {
                    System.out.println(evt.getPropertyName() + ":" + evt.getNewValue().toString());
                }
        );

        this.progressReporter.addPropertyChangeListener(
                ProgressEvent.OnDownloadComplete,
                evt -> {
                    updateJobState(JobState.Assembling);
                    updateProgress(1, 1);
                    System.out.println("Download completed");
                }
        );

        this.progressReporter.addPropertyChangeListener(
                ProgressEvent.OnDownloadFail,
                evt -> {
                    updateJobState(JobState.Failed);
                    updateProgress(0, 1);
                    System.out.println("Download failed");
                }
        );
    }

    public String getFilename() {
        return downloadJob.getFileName();
    }

    public String getTimestarted() {
        return dateFormat.format(this.startedTime);
    }

    public JobState getJobState() {
        return jobState;
    }

    private void updateJobState(JobState jobState) {
        if (this.jobState == jobState) return;

        this.jobState = jobState;
        updateMessage(jobState.toString());
    }

    public DownloadJob getDownloadJob() {
        return downloadJob;
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        updateJobState(JobState.Cancelled);
        downloadJob.interruptDownload();
    }

    @Override
    protected Void call() throws Exception {
        updateJobState(JobState.Started);
        if (!downloadJob.getFileName().isEmpty())
            updateTitle(downloadJob.getFileName());
        else
            updateTitle("Getting filename from server");

        downloadJob.prepareDownload();
        updateJobState(JobState.Downloading);
        this.fileSize = downloadJob.getFileSize();
        updateTitle(downloadJob.getFileName());
        System.out.println("started downloading " + downloadJob.getFileName());
        downloadJob.download();
        updateJobState(JobState.Assembling);
        downloadJob.moveToDestination();
        updateJobState(JobState.Completed);
        return null;
    }

    public int getId() {
        return id;
    }
}
