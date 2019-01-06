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

package org.octopus.core.misc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ProgressReporter {
    private PropertyChangeSupport propertyChangeSupport;
    private AtomicLong receivedBytes = new AtomicLong(0);
    private HashMap<Integer, DownloadState> downloadStateHashMap;
    private ConcurrentHashMap<Integer, AtomicLong> downloadCompletions;
    private int completedDownloads = 0;

    public ProgressReporter() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        downloadStateHashMap = new HashMap<>();
        downloadCompletions = new ConcurrentHashMap<>();
    }

    public void addPropertyChangeListener(ProgressEvent event, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(event.name(), listener);
    }

    public void removePropertyChangeListener(ProgressEvent event, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(event.name(), listener);
    }

    public void accumulateReceivedBytes(int id, long amount) {
        downloadCompletions.putIfAbsent(id, new AtomicLong(0));
        downloadCompletions.get(id).addAndGet(amount);

        Long oldVal = receivedBytes.get();
        Long newVal = receivedBytes.addAndGet(amount);
        propertyChangeSupport.firePropertyChange(ProgressEvent.OnBytesReceived.name(), oldVal, newVal);
    }

    public void setReceivedBytesForTask(int id, long amount) {
        this.downloadCompletions.putIfAbsent(id, new AtomicLong(amount));
    }

    public void updateState(int id, DownloadState state) {
        this.propertyChangeSupport.fireIndexedPropertyChange(
                ProgressEvent.OnStatusChanged.name(),
                id,
                downloadStateHashMap.getOrDefault(id, DownloadState.IN_PROGRESS),
                state
        );

        this.downloadStateHashMap.put(id, state);

        if (state == DownloadState.COMPLETED) completedDownloads++;

        if (isDownloadCompleted())
            propertyChangeSupport.firePropertyChange(ProgressEvent.OnDownloadComplete.name(), false, true);
    }

    private boolean isDownloadCompleted() {
        return completedDownloads == downloadCompletions.size();
    }

    public long getReceivedBytes() {
        return receivedBytes.get();
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes.set(receivedBytes);
    }

    public HashMap<Integer, DownloadState> getDownloadStateHashMap() {
        return downloadStateHashMap;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public HashMap<Integer, Long> getDownloadCompletions() {
        HashMap<Integer, Long> map = new HashMap<>();
        for (Map.Entry<Integer, AtomicLong> entry : downloadCompletions.entrySet()) {
            map.put(entry.getKey(), entry.getValue().longValue());
        }
        return map;
    }
}
