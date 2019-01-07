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

package org.octopus.dialogs.newdownload;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.octopus.alerts.CommonAlerts;
import org.octopus.downloads.DownloadJob;

import java.net.MalformedURLException;
import java.net.URL;

public class AddNewDownloadDialog extends Dialog<DownloadJob> {
    private ButtonType downloadButtonType;
    private AddNewDownloadController controller;

    public AddNewDownloadDialog() {
        setTitle("Add new download");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/dialogs/new-download-dialog.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            controller.setRoot((Stage) getDialogPane().getScene().getWindow());

            downloadButtonType = new ButtonType("Download", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            getDialogPane().getButtonTypes().addAll(downloadButtonType, cancelButtonType);
            getDialogPane().setContent(root);

        } catch (Exception e) {
            Alert alert = CommonAlerts.StackTraceAlert("Error", "Something went wrong", e.getMessage(), e);
            alert.showAndWait();
            System.exit(1);
        }

        // TODO validation
        setResultConverter(buttonType -> {
            if (buttonType == downloadButtonType) {
                try {
                    URL url = new URL(controller.getAddress());
                    return new DownloadJob(url, controller.getBaseDirectory(), controller.getName());
                } catch (MalformedURLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("URL is not valid");
                    alert.setContentText("The url " + controller.getAddress() + " is not a valid url");
                    alert.showAndWait();
                    return null;
                }
            }

            return null;
        });
    }
}
