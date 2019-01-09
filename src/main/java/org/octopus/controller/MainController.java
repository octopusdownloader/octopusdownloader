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

package org.octopus.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.octopus.dialogs.newdownload.AddNewDownloadDialog;
import org.octopus.dialogs.setting.OctupusSettingDialog;
import org.octopus.downloads.DownloadManager;
import org.octopus.downloads.DownloadTask;
import org.octopus.settings.OctopusSettings;

public class MainController {
    public Button addDownloadButton;
    public TableView tableView;

    public TableColumn filename;
    public TableColumn status;
    public TableColumn progress;
    public TableColumn timestarted;

    public Button settingButton;
    public Button startDownloadButton;
    public Button stopDownloadButton;
    public Button cancelDownloadButton;
    public Button downloadInfoButton;
    public Button deleteDownloadButton;

    private DownloadTask selectedTask = null;

    @FXML
    private void initialize() {
        initializeTable();
        //initializing the setting instance
        OctopusSettings.getInstance();
    }

    @SuppressWarnings("unchecked")
    private void initializeTable() {
        tableView.setEditable(false);
        tableView.setPlaceholder(new Label("Such empty :("));

        filename.setCellValueFactory(new PropertyValueFactory<DownloadTask, String>("title"));
        status.setCellValueFactory(new PropertyValueFactory<DownloadTask, String>("message"));
        timestarted.setCellValueFactory(new PropertyValueFactory<DownloadTask, String>("timestarted"));

        progress.setCellValueFactory(new PropertyValueFactory<DownloadTask, Double>("progress"));
        progress.setCellFactory(ProgressBarTableCell.<DownloadTask>forTableColumn());

        timestarted.setSortType(TableColumn.SortType.ASCENDING);

        tableView.setOnMouseClicked(event -> {
            selectedTask = (DownloadTask) tableView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                controlToolbarVisibility(selectedTask);
            }
        });
    }

    private void controlToolbarVisibility(DownloadTask task) {
        switch (task.getJobState()) {
            case Paused:
                downloadInfoButton.setDisable(false);
                cancelDownloadButton.setDisable(false);
                deleteDownloadButton.setDisable(false);
                stopDownloadButton.setDisable(false);
                startDownloadButton.setDisable(false); //start
                break;

            case Failed:
            case Cancelled:
                downloadInfoButton.setDisable(false);
                cancelDownloadButton.setDisable(true);
                deleteDownloadButton.setDisable(false);
                stopDownloadButton.setDisable(true);
                startDownloadButton.setDisable(true);
                break;

            case Assembling:
            case Completed:
                downloadInfoButton.setDisable(false);
                cancelDownloadButton.setDisable(true);
                deleteDownloadButton.setDisable(false);
                stopDownloadButton.setDisable(true);
                startDownloadButton.setDisable(true);
                break;

            case Started:
            case Downloading:
                downloadInfoButton.setDisable(false);
                cancelDownloadButton.setDisable(false);
                deleteDownloadButton.setDisable(true);
                stopDownloadButton.setDisable(false);
                startDownloadButton.setDisable(false);
                break;

            default:
                downloadInfoButton.setDisable(true);
                cancelDownloadButton.setDisable(true);
                deleteDownloadButton.setDisable(true);
                stopDownloadButton.setDisable(true);
                startDownloadButton.setDisable(true);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void addDownload(DownloadTask task) {
        tableView.getItems().add(task);
    }

    public void openAddNewDownloadDialog(MouseEvent mouseEvent) {
        new AddNewDownloadDialog()
                .showAndWait()
                .ifPresent(downloadJob -> {
                    try {
                        addDownload(DownloadManager.getInstance().addDownload(downloadJob));
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error downloading file");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                });
    }

    public void openSettingDialog(MouseEvent mouseEvent) {
        new OctupusSettingDialog()
                .showAndWait()
                .ifPresent(OctopusSettings::SaveSettings);
    }

    public void onDelete(MouseEvent mouseEvent) {
        if (selectedTask == null) return;

        selectedTask.getDownloadJob().deleteDownload();
        tableView.getItems().remove(selectedTask);
        selectedTask = null;
    }
}
