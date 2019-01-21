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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.octopus.alerts.CommonAlerts;
import org.octopus.settings.OctopusGeneralSettings;
import org.octopus.settings.OctopusProxySettings;
import org.octopus.settings.OctopusSettings;

public class OctupusSettingDialog extends Dialog<OctopusSettings> {

    ButtonType applyButtonType;
    ButtonType restorDefaultButtonType;
    private OctupusSettingController controller;
    private OctopusSettings octopusSettings = OctopusSettings.getInstance();

    public OctupusSettingDialog() {
        setTitle("Octopus Settings");

        try {

            FXMLLoader settingLoader = new FXMLLoader(getClass().getResource("/scenes/dialogs/setting-dialog.fxml"));
            Parent root = settingLoader.load();
            controller = settingLoader.getController();
            controller.setRoot((Stage) getDialogPane().getScene().getWindow());

            applyButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
            restorDefaultButtonType = new ButtonType("Restore", ButtonBar.ButtonData.LEFT);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            getDialogPane().getButtonTypes().addAll(restorDefaultButtonType, applyButtonType, cancelButtonType);
            getDialogPane().setContent(root);
        } catch (Exception e) {
            Alert alert = CommonAlerts.StackTraceAlert("Error", "Something went wrong", e.getMessage(), e);
            alert.showAndWait();
            System.exit(1);
        }

        setResultConverter(buttonType -> {
            if (buttonType == applyButtonType) {
                setProxySettings(octopusSettings, true);
                setGeneralSettings(octopusSettings, true);
            } else if (buttonType == restorDefaultButtonType) {
                setProxySettings(octopusSettings, false);
                setGeneralSettings(octopusSettings, false);
            } else return null;
            return octopusSettings;

        });
    }

    private void setProxySettings(OctopusSettings octopusSettings, boolean setProxy) {
        String host, port, proxytype;
        RadioButton radioButton = (RadioButton) controller.toggleGroup.getSelectedToggle();
        OctopusProxySettings octopusProxySettings = octopusSettings.getProxySettings();
        if (!setProxy) {
            octopusProxySettings.setProxyType(null);
            return;
        }

        if (radioButton.getId().equals(controller.buttonHttp.getId())) {
            host = controller.httpHostText.getText();
            port = controller.httpPortText.getText();
            proxytype = "http";
        } else {
            host = controller.socketHostText.getText();
            port = controller.socketPortText.getText();
            proxytype = "socket";
        }
        octopusProxySettings.setHost(host);
        octopusProxySettings.setPort(port);
        octopusProxySettings.setProxyType(proxytype);
        octopusSettings.setProxySettings(octopusProxySettings);
    }

    private void setGeneralSettings(OctopusSettings octopusSettings, boolean setGeneralSetting) {
        if (!setGeneralSetting) {
            octopusSettings.setGeneralSettings(new OctopusGeneralSettings());
            return;
        }
        OctopusGeneralSettings generalSettings = octopusSettings.getGeneralSettings();
        generalSettings.setTempDownloadpath(controller.buttonTempDownloadPath.getText());
        generalSettings.setMultipartsize(Integer.parseInt(controller.buttonMultipartSize.getText()));
        generalSettings.setBuffersize(Integer.parseInt(controller.buttonBufferSize.getText()));
        generalSettings.setSetGeneralSetting(true);

        //is this really needed?
        octopusSettings.setGeneralSettings(generalSettings);
    }
}
