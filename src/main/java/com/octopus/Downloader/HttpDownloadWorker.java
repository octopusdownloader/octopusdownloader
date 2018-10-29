/*
 * MIT License
 *
 * Copyright (c) 2018.
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

package com.octopus.Downloader;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloadWorker extends DownloadWorker {

    /**
     * @param url
     * @param start
     * @param end
     * @param filename
     */
    public HttpDownloadWorker(URL url, int start, int end, String filename) {
        super(url, start, end, filename);
    }


    @Override
    public void run() {
        BufferedInputStream in = null;
        RandomAccessFile rfile = null;

        try {
            //creates a http connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //setting header to start and end byte range to receive
            String byteRange = start + "-" + end;
            con.setRequestProperty("Range", "bytes=" + byteRange);

            // connect to server
            con.connect();

            //if response code is not 200 range file is missing or error
            if (con.getResponseCode() / 100 != 2) throw new Error("not a 200 code");

            in = new BufferedInputStream(con.getInputStream());

            rfile = new RandomAccessFile(filename, "rw");
            rfile.seek(start);

            //setting buffer to read a data of BUFFER_SIZE
            byte data[] = new byte[BUFFER_SIZE];

            int numread;

            while ((numread = in.read(data, 0, BUFFER_SIZE)) != -1) {
                rfile.write(data, 0, numread);

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rfile != null) rfile.close();
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
