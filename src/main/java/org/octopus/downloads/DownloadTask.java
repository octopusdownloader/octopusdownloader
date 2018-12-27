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

package org.octopus.downloads;

import javafx.concurrent.Task;
import org.octopus.core.Downloader;
import org.octopus.core.misc.ProgressReporter;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadTask extends Task<Void> {
    private ExecutorService executorService;
    private List<Future<Long>> futures;
    private ArrayList<Downloader> downloaders;
    private ProgressReporter progressReporter;
    private int id;
    private long maxSize;

    public DownloadTask(int id, ArrayList<Downloader> downloaders, long maxSize) {
        this.id = id;
        this.downloaders = downloaders;
        this.progressReporter = new ProgressReporter();
        this.maxSize = maxSize;
        this.executorService = Executors.newCachedThreadPool();
        this.futures = new ArrayList<>();

        progressReporter.addPropertyChangeListener(
                ProgressReporter.OnBytesReceived,
                evt -> updateProgress((Long) evt.getNewValue(), this.maxSize)
        );
    }

    @Override
    protected Void call() throws Exception {
        for (Downloader downloader : downloaders) {
            futures.add(executorService.submit(downloader));
        }

        // TODO handle exceptions
        for (Future<Long> future: futures) {

        }


        return null;
    }
}
