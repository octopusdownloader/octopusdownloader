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

package org.octopus.settings;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class OctupusSettingUnitTest {

    private static final String ROOT = System.getProperty("user.home");
    private static final String DIRECTORY = ".octopus";
    private static final String HOST = "cachex.pdn.ac.lk";
    private static final String PORT = "3128";
    private static final String PROXYTYPE = "http";
    private static final String PROXY_FILENAME = "proxy_setting.ser";
    private static final Path PROXY_FILEPATH = Paths.get(ROOT, DIRECTORY, PROXY_FILENAME);


    //deleting folder n setting file
    @AfterClass
    public static void deleteFolder() {
        Path path = Paths.get(ROOT, DIRECTORY);
        System.out.println("Deleting");
        try {
            Files.deleteIfExists(PROXY_FILEPATH);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void ShouldWriteProxyObjectFile() {
        //OctopusMainSetting octopusMainSetting = new OctopusMainSetting();
        OctopusSettings octopusSettings = OctopusSettings.getInstance();
        OctopusProxySettings proxySettings = octopusSettings.getProxySettings();
        proxySettings.setHost(HOST);
        proxySettings.setPort(PORT);
        proxySettings.setProxyType(PROXYTYPE);
        octopusSettings.setProxySettings(proxySettings);
        octopusSettings.SaveSettings();
        assertTrue(Files.exists(PROXY_FILEPATH));
    }

    @After
    public void resetSingleton() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field instance = OctopusSettings.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void ShouldReadProxyObject() {
        OctopusSettings octopusSettings = OctopusSettings.getInstance();
        assertEquals(PORT, octopusSettings.getProxySettings().getPort());
        assertEquals(HOST, octopusSettings.getProxySettings().getHost());
        assertEquals(PROXYTYPE, octopusSettings.getProxySettings().getProxyType());
    }

    @Test
    public void HTTPProxyShouldSetNow() {
        assertEquals(HOST, System.getProperty("http.proxyHost"));
        assertEquals(PORT, System.getProperty("http.proxyPort"));
    }

}
