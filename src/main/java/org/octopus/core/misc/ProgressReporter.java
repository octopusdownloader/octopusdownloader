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
import java.util.concurrent.atomic.AtomicLong;

public class ProgressReporter {
    public static final String OnBytesReceived = "OnBytesReceived";
    public static final String OnStatusChanged = "OnStatusChanged";

    private PropertyChangeSupport propertyChangeSupport;
    private AtomicLong receivedBytes = new AtomicLong(0);
    private HashMap<Integer, DownloadState> downloadStateHashMap;
    private HashMap<Integer, Long> downloadCompletions;

    public ProgressReporter() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        downloadStateHashMap = new HashMap<>();
        downloadCompletions = new HashMap<>();
    }

    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }

    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }

    public void accumulateReceivedBytes(int id, long amount) {
        downloadCompletions.put(id, amount);
        Long oldVal = receivedBytes.get();
        receivedBytes.addAndGet(amount);
        propertyChangeSupport.firePropertyChange(OnBytesReceived, oldVal, receivedBytes);
    }

    public void updateState(int id, DownloadState state) {
        this.propertyChangeSupport.fireIndexedPropertyChange(
                OnStatusChanged,
                id,
                downloadStateHashMap.getOrDefault(id, DownloadState.UNKNOWN),
                state
        );

        this.downloadStateHashMap.put(id, state);
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
        return downloadCompletions;
    }
}
