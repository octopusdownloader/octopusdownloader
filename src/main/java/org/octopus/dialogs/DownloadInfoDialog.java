/*
 * MIT License
 *
 * Copyright (c) 2019 octopusdownloader
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

package org.octopus.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.octopus.downloads.DownloadJob;

public class DownloadInfoDialog extends Dialog<Void> {
    public DownloadInfoDialog(DownloadJob job) {
        setTitle("Download Info");
        setHeaderText("Download Job Details");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        Image image = new Image("images/icons8-info-50.png", 64.0, 64.0, true, true);
        setGraphic(new ImageView(image));

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(new Label("URL:"), 0, 0);
        grid.add(new Label(job.getUrl().toString()), 1, 0);

        grid.add(new Label("File:"), 0, 1);
        grid.add(new Label(job.getFileName()), 1, 1);

        grid.add(new Label("Saved to:"), 0, 2);
        grid.add(new Label(job.getBaseDirectory().toString()), 1, 2);

        grid.add(new Label("Downloaded:"), 0, 3);
        grid.add(new Label(job.getCompletedAmount() + " bytes"), 1, 3);

        if (job.getFileSize() > 0) {
            grid.add(new Label("File Size"), 0, 4);
            grid.add(new Label(job.getFileSize() + " bytes"), 1, 4);
        }

        getDialogPane().setContent(grid);
    }
}
