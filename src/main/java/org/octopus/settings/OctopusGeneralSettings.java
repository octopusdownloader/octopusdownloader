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

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OctopusGeneralSettings implements Serializable {

    private static final String ROOT = System.getProperty("user.home");
    private static final String DIRECTORY = ".octopus";
    private static final String TEMP = "tmp";

    private static final long serialVersionUID = 1L;
    private int multipartsize, buffersize;
    private Path tempDownloadpath;
    private boolean setGeneralSetting;

    public OctopusGeneralSettings() {
        //default settings
        this.multipartsize = 8;
        this.buffersize = 1024;
        this.tempDownloadpath = Paths.get(ROOT, DIRECTORY, TEMP);
        this.setGeneralSetting = false;
    }

    public Path getTempDownloadpath() {
        return tempDownloadpath;
    }

    public void setTempDownloadpath(Path tempDownloadpath) {
        this.tempDownloadpath = tempDownloadpath;
    }

    public int getMultipartsize() {
        return multipartsize;
    }

    public void setMultipartsize(int multipartsize) {
        this.multipartsize = multipartsize;
    }

    public int getBuffersize() {
        return buffersize;
    }

    public void setBuffersize(int buffersize) {
        this.buffersize = buffersize;
    }

    public boolean isSetGeneralSetting() {
        return setGeneralSetting;
    }

    public void setSetGeneralSetting(boolean setGeneralSetting) {
        this.setGeneralSetting = setGeneralSetting;
    }

    public Object readResolve() {
        return this;
    }
}
