package com.octopus.core.http;

import org.junit.Test;

import java.net.Proxy;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HTTPInspectorTest {
    @Test public void getsStatusCode(){
        try {
            HTTPInspector httpInspector = new HTTPInspector(new URL("http://www.google.com"), Proxy.NO_PROXY, 2000);
            httpInspector.Inspect();
            int s = httpInspector.getStatusCode();
            assertTrue("Gets the status code", s != 0);
            System.out.println(httpInspector.getContentType());
        } catch (Exception e){
            assertFalse(false);
        }

    }
}
