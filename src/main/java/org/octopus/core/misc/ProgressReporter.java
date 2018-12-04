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

public class ProgressReporter {
    public static final String OnBytesReceived = "OnBytesReceived";
    public static final String OnStatusChanged = "OnStatusChanged";

    private PropertyChangeSupport propertyChangeSupport;
    private long receivedBytes = 0;
    private HashMap<Integer, DownloadState> downloadStateHashMap;

    public ProgressReporter() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        downloadStateHashMap = new HashMap<>();
    }

    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }

    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }

    public void accumulateReceivedBytes(long amount) {
        propertyChangeSupport.firePropertyChange(OnBytesReceived, this.receivedBytes, this.receivedBytes + amount);
        this.receivedBytes += amount;
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
        return receivedBytes;
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public HashMap<Integer, DownloadState> getDownloadStateHashMap() {
        return downloadStateHashMap;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }
}
