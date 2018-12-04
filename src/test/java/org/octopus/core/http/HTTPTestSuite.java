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

package org.octopus.core.http;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        HTTPInspectorTest.class,
        HTTPDownloadTest.class
})
public class HTTPTestSuite {
    private static ClientAndServer mockServer;

    @BeforeClass
    public static void startMockServer() throws Exception {
        System.out.println("Setting up mock server");

        mockServer = ClientAndServer.startClientAndServer(7088);

        mockServer.when(
                HttpRequest
                        .request()
                        .withMethod("GET")
                        .withPath("/path/to/file.txt")

        ).respond(
                HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withHeaders(
                                Header.header("Content-Length", 2),
                                Header.header("Accept-Ranges", "bytes")
                        )
                        .withBody("OK")
        );

        mockServer.when(
                HttpRequest
                        .request()
                        .withMethod("GET")
                        .withPath("/redirect/path/to/file")

        ).respond(
                HttpResponse
                        .response()
                        .withStatusCode(301)
                        .withHeader("Location", "http://localhost:7088/path/to/file1")
        );

        mockServer.when(
                HttpRequest
                        .request()
                        .withMethod("GET")
                        .withPath("/path/to/file1")

        ).respond(
                HttpResponse
                        .response()
                        .withStatusCode(301)
                        .withHeader("Location", "http://localhost:7088/path/to/file.txt")
        );

        Path image = Paths.get("src", "test", "resources", "image.jpg");
        byte[] imageBytes = Files.readAllBytes(image);
        mockServer.when(
                HttpRequest
                        .request()
                        .withMethod("GET")
                        .withPath("/data/image.jpg")
        ).respond(
                HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withHeader("Content-Length", Integer.toString(imageBytes.length))
                        .withHeader("Content-Type", "image/jpg")
                        .withBody(imageBytes)
        );

        mockServer.when(
                HttpRequest
                        .request()
                        .withMethod("GET")
                        .withPath("/data/partial/image.jpg")
                        .withHeader("Range", "bytes=0-1023")
        ).respond(
                HttpResponse
                        .response()
                        .withStatusCode(206)
                        .withHeader("Content-Length", "1024")
                        .withHeader("Content-Range", "0-1023/" + Integer.toString(imageBytes.length))
                        .withBody(Arrays.copyOfRange(imageBytes, 0, 1024))
        );

    }

    @AfterClass
    public static void stopMockServer() {
        System.out.println("Tearing down mock server");
        mockServer.stop();
    }
}
