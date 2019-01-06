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


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;


public class OctupusSettingController {

    public ToggleGroup toggleGroup;
    public TextField httpPortText;
    public TextField httpHostText;
    public TextField socketPortText;
    public TextField socketHostText;
    public RadioButton buttonHttp;
    public RadioButton buttonSocket;

    private Stage root;

    @FXML
    private void initialize() {
        toggleGroup = new ToggleGroup();
        buttonHttp.setToggleGroup(toggleGroup);
        buttonSocket.setToggleGroup(toggleGroup);
        buttonHttp.setSelected(true);

    }


    @FXML
    public void setFieldActive(ActionEvent event) {

        RadioButton radioButton = (RadioButton) toggleGroup.getSelectedToggle();
        System.out.println(radioButton.getId());
        if (radioButton.getId().equals(buttonHttp.getId())) {
            socketHostText.disableProperty();
            socketPortText.disableProperty();
            httpHostText.editableProperty();
            httpPortText.editableProperty();
        } else {
            socketHostText.editableProperty();
            socketPortText.editableProperty();
            httpHostText.disableProperty();
            httpPortText.disableProperty();
        }
    }

    public void setRoot(Stage root) {
        this.root = root;
    }
}
