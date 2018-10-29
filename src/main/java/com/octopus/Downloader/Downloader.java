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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Downloader {
    private URL url;
    private String filename;
    private int threadsize;

    /**
     * @param url
     * @param threadsize
     */
    public Downloader(String url, int threadsize) {
        try {
            this.url = new URL(url);
            this.threadsize = threadsize;
            this.filename = threadsize + url.substring(url.lastIndexOf("/") + 1);
            System.out.println(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    private void downloadFile() {

        ExecutorService executorService = Executors.newFixedThreadPool(threadsize);
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.connect();

            int fileLength = con.getContentLength();
            System.out.println("File size: " + fileLength);
            Long start = System.nanoTime();

            for (int x = 0; x < threadsize; x++) {
                executorService.submit(new HttpDownloadWorker(url, (fileLength * x) / threadsize, (fileLength * (x + 1)) / threadsize, filename) {
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);

            Long end = System.nanoTime();
            System.out.printf("Time Elapsed for Threadsize %d : %.4f ms\n", threadsize, (end - start) / 1e6);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
