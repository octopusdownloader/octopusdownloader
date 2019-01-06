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

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class OctupusSettingUnitTest {

    private static final String ROOT = System.getProperty("user.home");
    private static final String DIRECTORY = ".octopus";
    private static final String FILENAME = "setting.ser";
    private static final String HOST = "cachex.pdn.ac.lk";
    private static final String PORT = "3128";
    private static final Path FILEPATH = Paths.get(ROOT, DIRECTORY, FILENAME);

    //deleting folder n setting file
    @BeforeClass
    public static void deleteFolder() {
        Path path = Paths.get(ROOT, DIRECTORY);
        try {
            Files.deleteIfExists(FILEPATH);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void ShouldWriteObjectFile() {
        OctopusSettings octopusSettings = OctopusSettings.getInstance();
        OctupusProxySettings proxySettings = octopusSettings.getProxySettings();
        proxySettings.setHost(HOST);
        proxySettings.setPort(PORT);
        octopusSettings.setProxySettings(proxySettings);
        octopusSettings.SaveSettings();
        assertTrue(Files.exists(FILEPATH));
    }

    @Test
    public void ShouldReadObject() {
        OctopusSettings octopusSettings = OctopusSettings.getInstance();
        assertEquals(PORT, octopusSettings.getProxySettings().getPort());
        assertEquals(HOST, octopusSettings.getProxySettings().getHost());
    }


}
