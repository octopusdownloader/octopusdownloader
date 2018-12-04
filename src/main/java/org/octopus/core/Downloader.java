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

package org.octopus.core;

import org.octopus.core.misc.ProgressReporter;
import org.octopus.settings.OctopusSettings;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Downloader {
    private int id;
    private Downloadable downloadable;
    private Path file;
    private long bytesReceived;
    private ProgressReporter progressReporter;

    public Downloader(int id, Downloadable downloadable, Path file, ProgressReporter progressReporter) {
        this.id = id;
        this.downloadable = downloadable;
        this.file = file;
        this.progressReporter = progressReporter;
    }

    public void download() throws Exception {
        try (
                ReadableByteChannel inChan = Channels.newChannel(downloadable.getFileStream());
                FileChannel outChan = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)
        ) {
            // TODO Replace this with global setting for buffer size
            ByteBuffer byteBuffer = ByteBuffer.allocate(OctopusSettings.getDownloadBufferSize());
            int transferredBytes;
            while (inChan.read(byteBuffer) != -1) {
                byteBuffer.flip();
                transferredBytes = outChan.write(byteBuffer);
                bytesReceived += transferredBytes;
                progressReporter.accumulateReceivedBytes(transferredBytes);
                byteBuffer.rewind();
            }
        }
    }

    public long getBytesReceived() {
        return bytesReceived;
    }
}
