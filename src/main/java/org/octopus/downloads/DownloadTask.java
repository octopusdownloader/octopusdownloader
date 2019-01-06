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

public class DownloadTask extends Task<Void> {
    private ProgressReporter progressReporter;
    private int id;
    private long fileSize;
    private DownloadJob downloadJob;

    public DownloadTask(int id, DownloadJob job) {
        this.id = id;
        this.downloadJob = job;
        this.progressReporter = downloadJob.getProgressReporter();
        this.fileSize = downloadJob.getFileSize();

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
                    System.out.println("Download completed");
                    updateMessage("Done");
                    updateTitle("Download completed");
                    System.out.println("Total " + progressReporter.getReceivedBytes());
                }
        );
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        downloadJob.interruptDownload();
    }

    @Override
    protected Void call() throws Exception {
        updateMessage("Downloading");
        downloadJob.download();
        return null;
    }

    public int getId() {
        return id;
    }
}
