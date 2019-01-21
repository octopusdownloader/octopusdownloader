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

package org.octopus.dialogs.setting;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.octopus.settings.OctopusSettings;

import java.io.File;


public class OctupusSettingController {

    //setting tab
    public ToggleGroup toggleGroup;
    public TextField httpPortText;
    public TextField httpHostText;
    public TextField socketPortText;
    public TextField socketHostText;
    public RadioButton buttonHttp;
    public RadioButton buttonSocket;
    public OctopusSettings octopusSettings;

    //general setting tab
    public TextField buttonTempDownloadPath;
    public TextField buttonMultipartSize;
    public TextField buttonBufferSize;

    private Stage root;
    @FXML
    private void initialize() {
        octopusSettings = OctopusSettings.getInstance();
        toggleGroup = new ToggleGroup();
        buttonHttp.setToggleGroup(toggleGroup);
        buttonSocket.setToggleGroup(toggleGroup);

        //loading from proxy config file
        if (octopusSettings.getProxySettings().getProxyType() == null) {
            buttonHttp.setSelected(true);
            socketHostText.setDisable(true);
            socketPortText.setDisable(true);
        } else if (octopusSettings.getProxySettings().getProxyType().equals("http")) {
            buttonHttp.setSelected(true);
            httpHostText.setText(octopusSettings.getProxySettings().getHost());
            httpPortText.setText(octopusSettings.getProxySettings().getPort());
            socketHostText.setDisable(true);
            socketPortText.setDisable(true);
        } else {
            buttonSocket.setSelected(true);
            socketHostText.setText(octopusSettings.getProxySettings().getHost());
            socketPortText.setText(octopusSettings.getProxySettings().getPort());
            httpHostText.setDisable(false);
            httpPortText.setDisable(false);
        }

        buttonBufferSize.setText(String.valueOf(octopusSettings.getGeneralSettings().getBuffersize()));
        buttonMultipartSize.setText(String.valueOf(octopusSettings.getGeneralSettings().getMultipartsize()));
        buttonTempDownloadPath.setText(octopusSettings.getGeneralSettings().getTempDownloadpath().toString());
    }


    @FXML
    public void setFieldActive(ActionEvent event) {

        RadioButton radioButton = (RadioButton) toggleGroup.getSelectedToggle();
        if (radioButton.getId().equals(buttonHttp.getId())) {
            socketHostText.setDisable(true);
            socketPortText.setDisable(true);
//            socketPortText.clear();
//            socketHostText.clear();
            httpHostText.setDisable(false);
            httpPortText.setDisable(false);
        } else {
            socketHostText.setDisable(false);
            socketPortText.setDisable(false);
            httpHostText.setDisable(true);
            httpPortText.setDisable(true);
//            httpHostText.clear();
//            httpPortText.clear();
        }
    }

    public void openDirectoryPicker(ActionEvent actionEvent) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDir = directoryChooser.showDialog(root);

        if (selectedDir != null) {
            buttonTempDownloadPath.setText(selectedDir.getAbsolutePath());
        }
    }
    public void setRoot(Stage root) {
        this.root = root;
    }
}
