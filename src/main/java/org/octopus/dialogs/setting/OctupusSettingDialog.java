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

package org.octopus.dialogs.setting;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.octopus.alerts.CommonAlerts;

public class OctupusSettingDialog extends Dialog {

    ButtonType applyButtonType;
    private OctupusSettingController controller;

    public OctupusSettingDialog() {
        setTitle("Settings");

        try {
            FXMLLoader settingLoader = new FXMLLoader(getClass().getResource("/scenes/dialogs/setting-dialog.fxml"));
            Parent root = settingLoader.load();
            controller = settingLoader.getController();
            controller.setRoot((Stage) getDialogPane().getScene().getWindow());

            applyButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);
            getDialogPane().setContent(root);
        } catch (Exception e) {
            Alert alert = CommonAlerts.StackTraceAlert("Error", "Something went wrong", e.getMessage(), e);
            alert.showAndWait();
            System.exit(1);
        }
    }
}
