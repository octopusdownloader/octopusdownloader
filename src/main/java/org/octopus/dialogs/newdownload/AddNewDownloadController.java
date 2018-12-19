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

package org.octopus.dialogs.newdownload;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AddNewDownloadController {
    public TextField addressText;
    public TextField downloadDirectoryText;
    public TextField nameText;

    Path downloadDir;
    private Stage root;

    @FXML
    public void initialize() {
        // Get URLs from Clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String data = clipboard.getString();
        try {
            new URL(data);
            addressText.setText(data);
        } catch (MalformedURLException e) {
            // do nothing
        }

        // TODO add category picker here
        // Get default download folder
        downloadDir = Paths.get(System.getProperty("user.home"), "Downloads");
        downloadDirectoryText.setText(downloadDir.toString());
    }

    public void setRoot(Stage root) {
        this.root = root;
    }

    public void openDirectoryPicker(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = new File(downloadDir.toUri());
        directoryChooser.setInitialDirectory(dir);
        File selectedDir = directoryChooser.showDialog(root);

        if (selectedDir != null) {
            downloadDirectoryText.setText(selectedDir.getAbsolutePath());
            downloadDir = Paths.get(downloadDirectoryText.getText());
        }
    }

    public String getAddress() {
        return addressText.getText();
    }

    public Path getBaseDirectory() {
        return Paths.get(downloadDirectoryText.getText());
    }

    public String getName() {
        return nameText.getText();
    }
}
