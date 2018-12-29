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

    public synchronized int addDownload(DownloadTask task) {
        int dloadId = downloadID.incrementAndGet();
        taskHashMap.put(dloadId, task);
        executorService.submit(task);
        return dloadId;
    }

    public synchronized boolean stopDownload(int id) {
        DownloadTask task = taskHashMap.get(id);
        return task.cancel();
    }
}
