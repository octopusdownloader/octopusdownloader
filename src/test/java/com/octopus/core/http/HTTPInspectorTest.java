package com.octopus.core.http;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.net.Proxy;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class HTTPInspectorTest {
    private ClientAndServer mockServer;

    @Before
    public void startMockServer() {
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
    }

    @After
    public void stopMockServer() {
        mockServer.stop();
    }

    @Test
    public void shouldFindURLWhenNotRedirect() throws Exception {
        HTTPInspector httpInspector = new HTTPInspector(
                new URL("http://localhost:7088/path/to/file.txt"),
                Proxy.NO_PROXY,
                5000,
                5);


        URL finalURL = httpInspector.findRedirectedFinalURL(new URL("http://localhost:7088/path/to/file.txt"), 5);
        assertEquals("http://localhost:7088/path/to/file.txt", finalURL.toString());
    }

    @Test
    public void shouldFindURLWhenRedirect() throws Exception {
        HTTPInspector httpInspector = new HTTPInspector(
                new URL("http://localhost:7088/redirect/path/to/file"),
                Proxy.NO_PROXY,
                5000,
                5);


        URL finalURL = httpInspector.findRedirectedFinalURL(new URL("http://localhost:7088/redirect/path/to/file"), 5);
        assertEquals("http://localhost:7088/path/to/file.txt", finalURL.toString());
    }

    @Test(expected = RedirectLimitException.class)
    public void shouldNotFindURLWhenRedirectLimitExceeded() throws Exception {
        HTTPInspector httpInspector = new HTTPInspector(
                new URL("http://localhost:7088/redirect/path/to/file"),
                Proxy.NO_PROXY,
                5000,
                5);


        URL finalURL = httpInspector.findRedirectedFinalURL(new URL("http://localhost:7088/redirect/path/to/file"), 1);
        assertEquals("http://localhost:7088/path/to/file.txt", finalURL.toString());
    }
}
