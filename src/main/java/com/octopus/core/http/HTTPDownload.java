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

package com.octopus.core.http;

import com.octopus.core.Downloadable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class HTTPDownload implements Downloadable {
    private URL url;
    private Path file;
    private long size;
    private long from;
    private long to;
    private long receivedBytes;

    public HTTPDownload(URL url, Path file, long size, long from, long to) {
        this.url = url;
        this.file = file;
        this.size = size;
        this.from = from;
        this.to = to;
    }

    @Override
    public void download() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestProperty("Range", getRange());
        urlConnection.connect();
        int respCode = urlConnection.getResponseCode();

        switch (respCode) {
            case 416:
                throw new HTTPOutOfRangeException("Out of range");

            case 200:
            case 206:
                startDownload(urlConnection.getInputStream());
                break;

            default:
                throw new HTTPException("HTTP Error " + respCode);
        }
    }

    void startDownload(InputStream inputStream) throws Exception {
        ReadableByteChannel inChan = Channels.newChannel(inputStream);
        FileChannel outChan = FileChannel.open(file,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND,
                StandardOpenOption.WRITE
        );

        long startTime = System.nanoTime();
        ByteBuffer byteBuffer = ByteBuffer.allocate(10240);
        while (inChan.read(byteBuffer) != -1) {
            byteBuffer.flip();
            int n = outChan.write(byteBuffer);
            byteBuffer.rewind();
            double elapsed = (System.nanoTime() - startTime) / 1e9;
            System.out.println("Transferred = " + n + "bytes Transfer rate = " + (double) n / elapsed);
            receivedBytes += n;
        }

        inChan.close();
        outChan.close();
        byteBuffer.clear();
    }

    @Override
    public long receivedBytes() {
        return receivedBytes;
    }

    String getRange() {
        return "bytes=" + from + "-" + (to == 0 ? "" : to);
    }
}
