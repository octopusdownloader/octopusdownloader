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

package org.octopus.settings;

import org.octopus.alerts.CommonAlerts;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OctopusSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String ROOT = System.getProperty("user.home");
    private final String DIRECTORY = ".octopus";
    private final String FILENAME = "setting.ser";
    private final Path FILEPATH = Paths.get(ROOT, DIRECTORY, FILENAME);
    private OctupusGeneralSettings generalSettings;
    private OctupusProxySettings proxySettings;

    //eager initialization
    private volatile static OctopusSettings instance = new OctopusSettings();


    private OctopusSettings() {

        this.generalSettings = new OctupusGeneralSettings();
        this.proxySettings = new OctupusProxySettings();
        //check the file is available
        if (!checkFileAvailability()) {
            makedir();
        } else {
            //else load from the file
            OctopusSettings settings = DeserializedFromXML(FILEPATH);
            this.proxySettings = settings.proxySettings;
            this.generalSettings = settings.generalSettings;
        }
    }

    public static OctopusSettings getInstance() {
        return instance;
    }

    public void SaveSettings() {
        try {
            SerializetoObject(FILEPATH);
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


    private void SerializetoObject(Path path) throws NullPointerException, IOException {

        ObjectOutput encoder = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path.toString())));
        encoder.writeObject(instance.toString());
        encoder.close();

    }

    private OctopusSettings DeserializedFromXML(Path path) {

        OctopusSettings setting = null;
        try {
            ObjectInput decoder = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path.toString())));
            setting = (OctopusSettings) decoder.readObject();
            decoder.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File  not found");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: Classnotfound Exception");
        } catch (IOException e) {
            System.out.println("ERROR: Deserialize IO Exception");
        }
        return setting;
    }

    private boolean checkFileAvailability() {
        Path path = Paths.get(ROOT, DIRECTORY, FILENAME);
        System.out.println(Files.exists(path));
        return Files.exists(path);
    }

    private void makedir() {
        Path path = Paths.get(ROOT, DIRECTORY);
        try {
            Files.createDirectories(path);
            System.out.println("Folders " + Files.exists(path) + " " + path.toString());
        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
            CommonAlerts.StackTraceAlert("Error", "Cant Create Directory", "Octupus cant create the " +
                    "directory .Octupus in " + ROOT, e);
        }
    }

    public static String getUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";
    }

    public static int getDownloadBufferSize() {
        return 10240;
    }

    public Object readResolve() {
        return instance;
    }

    public OctupusGeneralSettings getGeneralSettings() {
        return generalSettings;
    }

    public void setGeneralSettings(OctupusGeneralSettings generalSettings) {
        this.generalSettings = generalSettings;
    }

    public OctupusProxySettings getProxySettings() {
        return proxySettings;
    }

    public void setProxySettings(OctupusProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }


}
