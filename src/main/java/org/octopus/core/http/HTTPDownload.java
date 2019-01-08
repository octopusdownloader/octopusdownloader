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

package org.octopus.core.http;

import org.octopus.core.Downloadable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPDownload implements Downloadable {
    private URL url;
    private long from;
    private long to;

    public HTTPDownload(URL url, long from, long to) {
        System.out.println("I download " + url);
        this.url = url;
        this.from = from;
        this.to = to;
    }

    public HTTPDownload(URL url, long from) {
        this.url = url;
        this.from = from;
        this.to = 0;
    }

    String getRange() {
        return "bytes=" + from + "-" + (to == 0 ? "" : to);
    }

    /**
     * Returns the input stream for a file
     *
     * @return InputStream
     * @throws Exception HTTP Exception
     */
    @Override
    public InputStream getFileStream() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestMethod("GET");
        if (to != 0) urlConnection.setRequestProperty("Range", getRange());
        urlConnection.connect();
        int respCode = urlConnection.getResponseCode();

        switch (respCode) {
            case 200:
            case 206:
                return urlConnection.getInputStream();

            default:
                throw new HTTPException("HTTP Error " + respCode);
        }
    }

    public URL getUrl() {
        return url;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }
}
