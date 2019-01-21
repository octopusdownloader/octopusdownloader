/*
 * MIT License (MIT)
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


import org.octopus.alerts.CommonAlerts;
import org.octopus.core.proxy.ProxySetting;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OctopusSettings {


    //
    private static final String ROOT = System.getProperty("user.home");
    private static final String DIRECTORY = ".octopus";
    private static final String PROXY_FILENAME = "proxy_setting.ser";
    private static final String GENARAL_FILENAME = "general_setting.ser";
    private static final Path PROXY_FILEPATH = Paths.get(ROOT, DIRECTORY, PROXY_FILENAME);
    private static final Path GENERAL_FILEPATH = Paths.get(ROOT, DIRECTORY, GENARAL_FILENAME);

    private String currentDownloadPath = ROOT;
    //null initialization
    private static volatile OctopusSettings instance = null;
    private OctopusGeneralSettings generalSettings;
    private OctopusProxySettings proxySettings;


    private OctopusSettings() {

        //check the file is available
        if (!checkProxyFileAvailability()) {
            makedir();
            this.proxySettings = new OctopusProxySettings(null);
        } else {
            ///else load from the file
            this.proxySettings = Deserialize(PROXY_FILEPATH, OctopusProxySettings.class);

            //setting proxy
            if (proxySettings.getProxyType() != null)
                if (proxySettings.getProxyType().equals("http")) {
                    ProxySetting.setHttpProxy(proxySettings.getHost(), proxySettings.getPort());
                } else {
                    ProxySetting.setSocketProxy(proxySettings.getHost(), proxySettings.getPort());
                }
        }
        if (!checkGeneralFileAvailability()) {
            this.generalSettings = new OctopusGeneralSettings();
        } else {
            this.generalSettings = Deserialize(GENERAL_FILEPATH, OctopusGeneralSettings.class);
        }
    }

    public static OctopusSettings getInstance() {
        if (instance == null) {
            instance = new OctopusSettings();
        }
        return instance;

    }

    public void SaveSettings() {
        try {
            //setting proxy setting
            setProxySettings();
            //ToDo general setting
            System.out.println(generalSettings.getTempDownloadpath() + " " + generalSettings.getMultipartsize());
            SerializetoObject(GENERAL_FILEPATH, OctopusGeneralSettings.class, this.generalSettings);

            SerializetoObject(PROXY_FILEPATH, OctopusProxySettings.class, this.proxySettings);


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException fileNotFound) {
            fileNotFound.getMessage();
            fileNotFound.printStackTrace();
            System.out.println("ERROR: While Creating or Opening the File");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: Saving IO Exception ");
        }
    }

    private void setProxySettings() {
        if (proxySettings.getProxyType() == null) ProxySetting.unsetProxy();
        else if (proxySettings.getProxyType().equals("http"))
            ProxySetting.setHttpProxy(proxySettings.getHost(), proxySettings.getPort());
        else ProxySetting.setSocketProxy(proxySettings.getHost(), proxySettings.getPort());

    }

    @SuppressWarnings("unchecked")
    private <T> void SerializetoObject(Path path, Class<T> type, Object object) throws NullPointerException, IOException {
        ObjectOutput encoder = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path.toString())));
        System.out.println(type.cast(object).toString());
        encoder.writeObject(type.cast(object).toString());
        encoder.close();

    }

    @SuppressWarnings("unchecked")
    private <T> T Deserialize(Path path, Class<T> type) {

        T setting = null;
        try {
            ObjectInput decoder = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path.toString())));
            setting = (T) decoder.readObject();
            decoder.close();
        } catch (FileNotFoundException e) {
            CommonAlerts.StackTraceAlert("Error", "Cant Create Directory", "File Not Found in" +
                    " " + path.toString(), e);
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: Class not found Exception");
        } catch (IOException e) {
            CommonAlerts.StackTraceAlert("Error", "Cant Read the File", "Octupus cant read the " +
                    "directory .Octupus in " + ROOT, e);
        }
        return type.cast(setting);
    }

    private boolean checkProxyFileAvailability() {
        Path path = Paths.get(ROOT, DIRECTORY, PROXY_FILENAME);
        return Files.exists(path);
    }

    private boolean checkGeneralFileAvailability() {
        Path path = Paths.get(ROOT, DIRECTORY, GENARAL_FILENAME);
        return Files.exists(path);
    }

    //
    private void makedir() {
        Path path = Paths.get(ROOT, DIRECTORY);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
            CommonAlerts.StackTraceAlert("Error", "Cant Create Directory", "Octupus cant create the " +
                    "directory .Octupus in " + ROOT, e);
        }
    }

    public Path getTempDownloadBasepath() {
        return generalSettings.getTempDownloadpath();
    }

    public int getMaxDownloadParts() {
        return generalSettings.getMultipartsize();
    }

    public int getDownloadBufferSize() {
        return generalSettings.getBuffersize();
    }

    public String getUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";
    }


    public OctopusGeneralSettings getGeneralSettings() {
        return generalSettings;
    }

    public void setGeneralSettings(OctopusGeneralSettings generalSettings) {
        this.generalSettings = generalSettings;
    }

    public OctopusProxySettings getProxySettings() {
        return proxySettings;
    }

    public void setProxySettings(OctopusProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }


}
