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

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadManager {
    private static DownloadManager ourInstance = new DownloadManager();
    private AtomicInteger downloadID = new AtomicInteger(0);
    private HashMap<Integer, DownloadTask> taskHashMap;
    private ExecutorService executorService;

    private DownloadManager() {
        taskHashMap = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
    }

    public static DownloadManager getInstance() {
        if (ourInstance.executorService.isShutdown()) {
            ourInstance.executorService = Executors.newCachedThreadPool();
        }
        return ourInstance;
    }

    public synchronized DownloadTask addDownload(DownloadJob job) {
        int dloadId = downloadID.incrementAndGet();
        DownloadTask task = new DownloadTask(dloadId, job);
        taskHashMap.put(dloadId, task);
        executorService.submit(task);
        return task;
    }

    public synchronized boolean stopDownload(int id) {
        DownloadTask task = taskHashMap.get(id);
        return task.cancel();
    }

    public synchronized void cancelAll() {
        for (DownloadTask t : taskHashMap.values()) {
            t.cancel(true);
        }
        executorService.shutdownNow();
    }
}
